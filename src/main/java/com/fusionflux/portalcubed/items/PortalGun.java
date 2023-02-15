package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.PortalCubedGameRules;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.unascribed.lib39.recoil.api.DirectClickItem;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.*;
import java.util.function.BiFunction;


public class PortalGun extends Item implements DirectClickItem, DyeableItem {

    private static final Map<Pair<Vec3i, Vec3i>, List<List<Direction>>> FAIL_TRIES;
    private static final Map<Direction.AxisDirection, Double2DoubleFunction> FAIL_AXIS_DIRS = new EnumMap<>(Map.of(
        Direction.AxisDirection.NEGATIVE, Math::floor,
        Direction.AxisDirection.POSITIVE, Math::ceil
    ));

    static {
        final List<BiFunction<Direction, Direction, List<Direction>>> failTryFns = List.of(
            (u, r) -> List.of(r.getOpposite()),
            (u, r) -> List.of(u),
            (u, r) -> List.of(r),
            (u, r) -> List.of(u.getOpposite()),
            (u, r) -> List.of(r.getOpposite(), u),
            (u, r) -> List.of(r, u),
            (u, r) -> List.of(r, u.getOpposite()),
            (u, r) -> List.of(r.getOpposite(), u.getOpposite())
        );
        final ImmutableMap.Builder<Pair<Vec3i, Vec3i>, List<List<Direction>>> failTries = ImmutableMap.builder();
        for (final Direction u : Direction.values()) {
            for (final Direction r : Direction.values()) {
                final ImmutableList.Builder<List<Direction>> entry = ImmutableList.builder();
                for (final var fn : failTryFns) {
                    entry.add(fn.apply(u, r));
                }
                failTries.put(Pair.of(u.getVector(), r.getVector()), entry.build());
            }
        }
        FAIL_TRIES = failTries.build();
    }

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
            ExperimentalPortal originalPortal;
            NbtCompound portalsTag = tag.getCompound(world.getRegistryKey().toString());

            if (portalsTag.contains((leftClick ? "Left" : "Right") + "Portal")) {
                originalPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Left" : "Right") + "Portal"));
            } else {
                originalPortal = null;
            }
            portalHolder = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);

            ExperimentalPortal otherPortal;
            if (portalsTag.contains((leftClick ? "Right" : "Left") + "Portal")) {
                otherPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Right" : "Left") + "Portal"));
            } else {
                otherPortal = null;
            }

            Vec3i up;
            Vec3i normal;
            Vec3i right;
            Vec3d blockPos;

            HitResult hitResult = customRaycast(user, 128.0D, 0.0F);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                normal = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                if (normal.getY() == 0) {
                    up = new Vec3i(0, 1, 0);
                } else {
                    up = user.getHorizontalFacing().getVector();
                }
                right = up.crossProduct(normal);

                final int alignment = world.getGameRules().getInt(PortalCubedGameRules.PORTAL_ALIGNMENT);
                if (alignment == 0) {
                    blockPos = hitResult.getPos();
                } else {
                    blockPos = new Vec3d(
                        Math.round(hitResult.getPos().x * alignment) / (double)alignment,
                        Math.round(hitResult.getPos().y * alignment) / (double)alignment,
                        Math.round(hitResult.getPos().z * alignment) / (double)alignment
                    );
                }

                Vec3d portalPos1 = calcPos(blockPos, normal);

                assert portalHolder != null;
                portalHolder.setOriginPos(portalPos1);
                portalHolder.setDestination(Optional.of(portalPos1));

                var rotAngles = IPQuaternion.getPitchYawFromRotation(getPortalOrientationQuaternion(Vec3d.of(right), Vec3d.of(up)));
                portalHolder.setYaw(rotAngles.getLeft().floatValue() + (90 * up.getX()));
                portalHolder.setPitch(rotAngles.getRight().floatValue());
                portalHolder.setRoll((rotAngles.getRight().floatValue() + (90)) * up.getX());
                portalHolder.setColor(this.getSidedColor(stack));
                portalHolder.setOrientation(Vec3d.of(right), Vec3d.of(up).multiply(-1));

                //noinspection DataFlowIssue
                final Direction.Axis hAxis = Direction.fromVector(new BlockPos(right)).getAxis();
                findCorrectOrientation:
                if (!portalHolder.validate()) {
                    for (final var try_ : FAIL_TRIES.get(Pair.of(up, right))) {
                        Vec3d tryPos = portalPos1;
                        for (final Direction part : try_) {
                            double newAxis = FAIL_AXIS_DIRS.get(part.getDirection()).get(tryPos.getComponentAlongAxis(part.getAxis()));
                            if (part.getAxis() == hAxis) {
                                newAxis += part.getDirection() == Direction.AxisDirection.POSITIVE ? -0.5 : 0.5;
                            }
                            tryPos = tryPos.withAxis(part.getAxis(), newAxis);
                        }
                        portalHolder.setOriginPos(tryPos);
                        if (portalHolder.validate()) {
                            break findCorrectOrientation;
                        }
                    }
                    world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    return;
                }

                final List<ExperimentalPortal> overlappingPortals = world.getEntitiesByType(
                    PortalCubedEntities.EXPERIMENTAL_PORTAL,
                    portalHolder.getBoundingBox(),
                    p -> p != originalPortal && vectorsEqual(p.getNormal(), portalHolder.getNormal())
                );

                if (!overlappingPortals.isEmpty()) {
                    boolean bumpSuccess = false;
                    if (overlappingPortals.size() == 1) {
                        final ExperimentalPortal overlappingPortal = overlappingPortals.get(0);
                        if (overlappingPortal.getAxisW().equals(portalHolder.getAxisW())) {
                            final Direction.Axis axis = Objects.requireNonNull(Direction.fromVector(new BlockPos(right))).getAxis();
                            if (overlappingPortal.getOriginPos().getComponentAlongAxis(axis) < portalHolder.getOriginPos().getComponentAlongAxis(axis)) {
                                portalHolder.setOriginPos(portalHolder.getOriginPos().withAxis(axis, overlappingPortal.getOriginPos().getComponentAlongAxis(axis) + 1));
                            } else {
                                portalHolder.setOriginPos(portalHolder.getOriginPos().withAxis(axis, overlappingPortal.getOriginPos().getComponentAlongAxis(axis) - 1));
                            }
                            bumpSuccess = portalHolder.validate();
                        }
                    }
                    if (!bumpSuccess) {
                        world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                        return;
                    }
                }

                world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), leftClick ? PortalCubedSounds.FIRE_EVENT_PRIMARY : PortalCubedSounds.FIRE_EVENT_SECONDARY, SoundCategory.NEUTRAL, .3F, 1F);

                if (originalPortal == null) {
                    portalHolder.setLinkedPortalUUID(Optional.empty());
                } else {
                    CalledValues.removePortals(user, originalPortal.getUuid());
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

                    CalledValues.addPortals(user, portalHolder.getUuid());
                    if (!isOtherAuto) {
                        CalledValues.addPortals(user, otherPortal.getUuid());
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
    }

    /**
     * {@link Vec3d#equals} uses {@link Double#compare} to compare axes. {@link Double#compare}, however, treats 0.0 and
     * -0.0 as not equal.
     */
    private static boolean vectorsEqual(Vec3d a, Vec3d b) {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
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
        portal1.setOtherFacing(new Vec3d(portal2.getFacingDirection().getUnitVector().getX(), portal2.getFacingDirection().getUnitVector().getY(), portal2.getFacingDirection().getUnitVector().getZ()));
        portal1.setOtherAxisH(portal2.getAxisH().get());
        portal2.setDestination(Optional.of(portal1.getOriginPos()));
        portal2.setOtherFacing(new Vec3d(portal1.getFacingDirection().getUnitVector().getX(), portal1.getFacingDirection().getUnitVector().getY(), portal1.getFacingDirection().getUnitVector().getZ()));
        portal2.setOtherAxisH(portal1.getAxisH().get());
        portal1.setLinkedPortalUUID(Optional.of(portal2.getUuid()));
        portal2.setLinkedPortalUUID(Optional.of(portal1.getUuid()));

        portal1.getWorld().playSound(null, portal1.getPos().getX(), portal1.getPos().getY(), portal1.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, volume, 1F);
        portal2.getWorld().playSound(null, portal2.getPos().getX(), portal2.getPos().getY(), portal2.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, volume, 1F);
    }

    /**
     * @param hit     the position designated by the player's input for a given portal.
     * @param facing  the facing axial vector of the portal based on placement context.
     * @return a vector position specifying the portal's final position in the world.
     */
    private Vec3d calcPos(Vec3d hit, Vec3i facing) {
        double faceOffset = -0.01;
        return new Vec3d(
            ((hit.getX()) + faceOffset * facing.getX()), // x component
            ((hit.getY()) + faceOffset * facing.getY()), // y component
            ((hit.getZ()) + faceOffset * facing.getZ())  // z component
        );
    }

    public HitResult customRaycast(Entity user, double maxDistance, float tickDelta) {
        final Vec3d start = user.getCameraPosVec(tickDelta);
        final Vec3d rotation = user.getRotationVec(tickDelta);
        final Vec3d end = start.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
        final World world = user.world;
        final ShapeContext shapeContext = ShapeContext.of(user);
        return BlockView.raycast(
            start, end, null,
            (context, pos) -> {
                final BlockState block = world.getBlockState(pos);
                if (block.isIn(PortalCubedBlocks.PORTAL_NONSOLID)) {
                    return null;
                }
                final VoxelShape blockShape = block.isIn(PortalCubedBlocks.PORTAL_SOLID)
                    ? block.getOutlineShape(world, pos, shapeContext)
                    : block.getCollisionShape(world, pos, shapeContext);
                return world.raycastBlock(start, end, pos, blockShape, block);
            },
            context -> {
                final Vec3d offset = start.subtract(end);
                return BlockHitResult.createMissed(end, Direction.getFacing(offset.x, offset.y, offset.z), new BlockPos(end));
            }
        );
    }

    public static IPQuaternion getPortalOrientationQuaternion(
            Vec3d axisW, Vec3d axisH
    ) {
        Vec3d normal = axisW.crossProduct(axisH);

        return IPQuaternion.matrixToQuaternion(axisW, axisH, normal);
    }

}
