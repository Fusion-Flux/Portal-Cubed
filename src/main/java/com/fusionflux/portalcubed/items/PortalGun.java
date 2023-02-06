package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.unascribed.lib39.recoil.api.DirectClickItem;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;


public class PortalGun extends Item implements DirectClickItem, DyeableItem {

    public PortalGun(Settings settings) {
        super(settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound compoundTag = stack.getSubNbt("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : 0x1d86db;
    }

    public boolean isComplementary(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("complementary");
    }

    public int getSidedColor(ItemStack stack) {
        final int color = getColor(stack);
        return isComplementary(stack) ? 0xffffff - color + 1 : color;
    }

    public int getColorForHudHalf(ItemStack stack, boolean rightHalf) {
        final int color = getColor(stack);
        return rightHalf ? 0xffffff - color + 1 : color;
    }

    @ClientOnly
    public boolean isSideActive(ClientWorld world, ItemStack stack, boolean rightSide) {
        final NbtCompound portalsTag = stack.getOrCreateNbt().getCompound(world.getRegistryKey().toString());
        final String key = rightSide ? "RightPortal" : "LeftPortal";
        if (portalsTag == null || !portalsTag.containsUuid(key)) return false;
        final UUID uuid = portalsTag.getUuid(key);
        for (final Entity globalPortal : world.getEntities()) {
            if (globalPortal.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResult onDirectAttack(PlayerEntity user, Hand hand) {
        shoot(user.world, user, hand, true);
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult onDirectUse(PlayerEntity user, Hand hand) {
        shoot(user.world, user, hand, false);
        return ActionResult.CONSUME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    protected boolean allowLinkingToOther() {
        return false;
    }

    protected void shoot(World world, PlayerEntity user, Hand hand, boolean leftClick) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSpectator()) return;
        stack.getOrCreateNbt().putBoolean("complementary", !leftClick);
        if (!world.isClient) {
            NbtCompound tag = stack.getOrCreateNbt();

            ExperimentalPortal portalHolder;
            ExperimentalPortal originalPortal = null;
            NbtCompound portalsTag = tag.getCompound(world.getRegistryKey().toString());

            boolean portalExists = false;
            if (portalsTag.contains((leftClick ? "Left" : "Right") + "Portal")) {
                originalPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Left" : "Right") + "Portal"));
                if (originalPortal == null) {
                    portalHolder = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);
                } else {
                    portalHolder = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);
                    portalExists = true;
                }
            } else {
                portalHolder = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);
            }

            ExperimentalPortal otherPortal;
            if (portalsTag.contains((leftClick ? "Right" : "Left") + "Portal")) {
                otherPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Right" : "Left") + "Portal"));
            } else {
                otherPortal = null;
            }

            Vec3i up;
            Vec3i normal;
            Vec3i right;
            BlockPos blockPos;

            HitResult hitResult = customRaycast(user,128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) hitResult).getBlockPos();
                normal = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                if (normal.getY() == 0) {
                    up = new Vec3i(0, 1, 0);
                } else {
                    up = user.getHorizontalFacing().getVector();
                }
                right = up.crossProduct(normal);



                Vec3d portalPos1 = calcPos(blockPos, up, normal, right);

                if(!validPos(world,up,right,portalPos1)) {
                    for (int i = 1; i < 9; i++) {
                        Vec3d shiftedPortalPos = portalPos1;
                        switch (i) {
                            case 1 -> shiftedPortalPos = portalPos1.add(Vec3d.of(up).multiply(-1.0));
                            case 2 -> shiftedPortalPos = portalPos1.add(Vec3d.of(right).multiply(-1.0));
                            case 3 -> shiftedPortalPos = portalPos1.add(Vec3d.of(up));
                            case 4 -> shiftedPortalPos = portalPos1.add(Vec3d.of(right));
                            case 5 ->
                                shiftedPortalPos = portalPos1.add(Vec3d.of(right).multiply(-1.0)).add(Vec3d.of(up).multiply(-1.0));
                            case 6 -> shiftedPortalPos = portalPos1.add(Vec3d.of(right).multiply(-1.0)).add(Vec3d.of(up));
                            case 7 -> shiftedPortalPos = portalPos1.add(Vec3d.of(up)).add(Vec3d.of(right));
                            case 8 -> shiftedPortalPos = portalPos1.add(Vec3d.of(right)).add(Vec3d.of(up).multiply(-1.0));
                        }

                        if (validPos(world, up, right, shiftedPortalPos)) {
                            portalPos1 = shiftedPortalPos;
                            break;
                        }

                        if (i == 8) {
                            world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                            return;
                        }
                    }
                }

                world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), leftClick ? PortalCubedSounds.FIRE_EVENT_PRIMARY : PortalCubedSounds.FIRE_EVENT_SECONDARY, SoundCategory.NEUTRAL, .3F, 1F);
                if (portalHolder != null && portalHolder.isAlive()) {
                    world.playSound(null, portalHolder.getPos().getX(), portalHolder.getPos().getY(), portalHolder.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                }


                assert portalHolder != null;
                portalHolder.setOriginPos(portalPos1);
                portalHolder.setDestination(Optional.of(portalPos1));

                Pair<Double, Double> rotAngles = IPQuaternion.getPitchYawFromRotation(getPortalOrientationQuaternion(Vec3d.of(right), Vec3d.of(up)));
                portalHolder.setYaw(rotAngles.getLeft().floatValue() + (90 * up.getX()));
                portalHolder.setPitch(rotAngles.getRight().floatValue());
                portalHolder.setRoll((rotAngles.getRight().floatValue() + (90)) * up.getX());
                portalHolder.setColor(this.getSidedColor(stack));

                portalHolder.setOrientation(Vec3d.of(right),Vec3d.of(up).multiply(-1));

                if (!portalExists) {
                    portalHolder.setLinkedPortalUUID(Optional.empty());
                } else {
                    CalledValues.removePortals(user,originalPortal.getUuid());
                    originalPortal.kill();
                }
                world.spawnEntity(portalHolder);
                portalHolder.setOwnerUUID(Optional.of(user.getUuid()));
                CalledValues.addPortals(user, portalHolder.getUuid());
                final boolean isOtherAuto = otherPortal == null;
                if (isOtherAuto) {
                    otherPortal = getPotentialOpposite(
                        world, portalPos1, portalHolder, portalHolder.getColor(), allowLinkingToOther()
                    ).orElse(null);
                }
                if (otherPortal != null) {
                    linkPortals(portalHolder, otherPortal, 0.1f);

                    portalHolder.setOwnerUUID(Optional.of(user.getUuid()));
                    if (!isOtherAuto) {
                        otherPortal.setOwnerUUID(Optional.of(user.getUuid()));
                    }

                    CalledValues.addPortals(user,portalHolder.getUuid());
                    if (!isOtherAuto) {
                        CalledValues.addPortals(user,otherPortal.getUuid());
                    }
                }
            } else {
                world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                return;
            }

            portalsTag.putUuid((leftClick ? "Left" : "Right") + "Portal", portalHolder.getUuid());

            tag.put(world.getRegistryKey().toString(), portalsTag);
        } else {
            cancelClientMovement(user);
        }
        return;
    }

    @ClientOnly
    private static void cancelClientMovement(Entity user) {
        if (user instanceof ClientPlayerEntity clientPlayer) {
            if (clientPlayer.input.getMovementInput().lengthSquared() < 0.1 && user.getPitch() >= 88.0) {
                user.setVelocity(0, user.getVelocity().y, 0);
            }
        }
    }

    public static Optional<ExperimentalPortal> getPotentialOpposite(World world, Vec3d portalPos, @Nullable ExperimentalPortal ignore, int color, boolean includePlayerPortals) {
        return world.getEntitiesByType(
            PortalCubedEntities.EXPERIMENTAL_PORTAL,
            Box.of(portalPos, 256, 256, 256),
            p ->
                p != ignore &&
                    p.getColor() == 0xffffff - color + 1 &&
                    (includePlayerPortals || p.getOwnerUUID().isEmpty()) &&
                    !p.getActive()
        ).stream().min(Comparator.comparingDouble(p -> p.getOriginPos().squaredDistanceTo(portalPos)));
    }

    public static void linkPortals(ExperimentalPortal portal1, ExperimentalPortal portal2, float volume) {
        portal1.setDestination(Optional.of(portal2.getOriginPos()));
        portal1.setOtherFacing(new Vec3d(portal2.getFacingDirection().getUnitVector().getX(),portal2.getFacingDirection().getUnitVector().getY(),portal2.getFacingDirection().getUnitVector().getZ()));
        portal1.setOtherAxisH(portal2.getAxisH().get());
        portal2.setDestination(Optional.of(portal1.getOriginPos()));
        portal2.setOtherFacing(new Vec3d(portal1.getFacingDirection().getUnitVector().getX(),portal1.getFacingDirection().getUnitVector().getY(),portal1.getFacingDirection().getUnitVector().getZ()));
        portal2.setOtherAxisH(portal1.getAxisH().get());
        portal1.setLinkedPortalUUID(Optional.of(portal2.getUuid()));
        portal2.setLinkedPortalUUID(Optional.of(portal1.getUuid()));

        portal1.getWorld().playSound(null, portal1.getPos().getX(), portal1.getPos().getY(), portal1.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, volume, 1F);
        portal2.getWorld().playSound(null, portal2.getPos().getX(), portal2.getPos().getY(), portal2.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, volume, 1F);
    }

    private boolean validPos(World world, Vec3i up, Vec3i right, Vec3d portalPos1){
        Vec3d CalculatedAxisW;
        Vec3d CalculatedAxisH;
        Vec3d posNormal;
        CalculatedAxisW = Vec3d.of(right);
        CalculatedAxisH = Vec3d.of(up).multiply(-1);
        posNormal = CalculatedAxisW.crossProduct(CalculatedAxisH).normalize();
        Direction portalFacing = Direction.fromVector((int) posNormal.getX(), (int) posNormal.getY(), (int) posNormal.getZ());

        BlockPos topBehind = new BlockPos(
                portalPos1.getX() - CalculatedAxisW.crossProduct(CalculatedAxisH).getX(),
                portalPos1.getY() - CalculatedAxisW.crossProduct(CalculatedAxisH).getY(),
                portalPos1.getZ() - CalculatedAxisW.crossProduct(CalculatedAxisH).getZ());
        BlockPos bottomBehind = new BlockPos(
                portalPos1.getX() - CalculatedAxisW.crossProduct(CalculatedAxisH).getX() - Math.abs(CalculatedAxisH.getX()),
                portalPos1.getY() - CalculatedAxisW.crossProduct(CalculatedAxisH).getY() + CalculatedAxisH.getY(),
                portalPos1.getZ() - CalculatedAxisW.crossProduct(CalculatedAxisH).getZ() - Math.abs(CalculatedAxisH.getZ()));
        BlockPos bottom = new BlockPos(
                portalPos1.getX() - Math.abs(CalculatedAxisH.getX()),
                portalPos1.getY() + CalculatedAxisH.getY(),
                portalPos1.getZ() - Math.abs(CalculatedAxisH.getZ()));


        boolean topValidBlock=false;
        if(world.getBlockState(new BlockPos(portalPos1)).isIn(PortalCubedBlocks.GEL_CHECK_TAG)&&world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
            assert portalFacing != null;
            BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());
            topValidBlock = world.getBlockState(new BlockPos(portalPos1)).get(booleanProperty);
        }else if (!world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
            topValidBlock=true;
        }
        boolean bottomValidBlock=false;
        if(world.getBlockState(bottom).isIn(PortalCubedBlocks.GEL_CHECK_TAG)&&world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
            assert portalFacing != null;
            BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());
            bottomValidBlock = world.getBlockState(bottom).get(booleanProperty);
        }else if (!world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
            bottomValidBlock=true;
        }

        return (world.getBlockState(topBehind).isSideSolidFullSquare(world, topBehind, portalFacing)) &&
                (world.getBlockState(bottomBehind).isSideSolidFullSquare(world, bottomBehind, portalFacing) &&
                        topValidBlock &&
                        bottomValidBlock) &&
                ((world.getBlockState(new BlockPos(portalPos1)).isAir()) || world.getBlockState(new BlockPos(portalPos1)).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN)) && (world.getBlockState(bottom).isAir() || world.getBlockState(bottom).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN));
    }

    /**
     * @param hit     the position designated by the player's input for a given portal.
     * @param upright the upright axial vector of the portal based on placement context.
     * @param facing  the facing axial vector of the portal based on placement context.
     * @param cross   the cross product of upright x facing.
     * @return a vector position specifying the portal's final position in the world.
     */
    private Vec3d calcPos(BlockPos hit, Vec3i upright, Vec3i facing, Vec3i cross) {
        double upOffset = -0.5;
        double faceOffset = -0.510;
        double crossOffset = 0.0;
        return new Vec3d(
                ((hit.getX() + 0.5) + upOffset * upright.getX() + faceOffset * facing.getX() + crossOffset * cross.getX()), // x component
                ((hit.getY() + 0.5) + upOffset * upright.getY() + faceOffset * facing.getY() + crossOffset * cross.getY()), // y component
                ((hit.getZ() + 0.5) + upOffset * upright.getZ() + faceOffset * facing.getZ() + crossOffset * cross.getZ())  // z component
        );
    }


    public HitResult customRaycast(Entity user, double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d vec3d = user.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = user.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return user.world
                .raycast(
                        new RaycastContext(
                                vec3d,
                                vec3d3,
                                RaycastContext.ShapeType.COLLIDER,
                                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                                user
                        )
                );
    }

    public static IPQuaternion getPortalOrientationQuaternion(
            Vec3d axisW, Vec3d axisH
    ) {
        Vec3d normal = axisW.crossProduct(axisH);

        return IPQuaternion.matrixToQuaternion(axisW, axisH, normal);
    }

}