package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.PortalCubedGameRules;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.ClickHandlingItem;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.*;
import java.util.function.BiFunction;


public class PortalGun extends Item implements ClickHandlingItem, DyeableLeatherItem {
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
                failTries.put(Pair.of(u.getNormal(), r.getNormal()), entry.build());
            }
        }
        FAIL_TRIES = failTries.build();
    }

    public PortalGun(Properties settings) {
        super(settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : 0x1d86db;
    }

    public boolean isComplementary(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("complementary");
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
    public boolean isSideActive(ClientLevel level, ItemStack stack, boolean rightSide) {
        final CompoundTag portalsTag = stack.getOrCreateTag().getCompound(level.dimension().location().toString());
        final String key = rightSide ? "RightPortal" : "LeftPortal";
        if (!portalsTag.hasUUID(key)) return false;
        final UUID uuid = portalsTag.getUUID(key);
        if (((LevelExt)level).getEntityByUuid(uuid) != null) {
            return true;
        }
        final String otherKey = rightSide ? "LeftPortal" : "RightPortal";
        if (!portalsTag.hasUUID(otherKey)) return false;
        final UUID otherUuid = portalsTag.getUUID(otherKey);
        return ((LevelExt)level).getEntityByUuid(otherUuid) instanceof Portal portal && portal.getActive();
    }

    @Override
    public InteractionResult onLeftClick(Player user, InteractionHand hand) {
        shoot(user.level, user, hand, true);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult onRightClick(Player user, InteractionHand hand) {
        shoot(user.level, user, hand, false);
        return InteractionResult.CONSUME;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    protected boolean allowLinkingToOther() {
        return false;
    }

    protected void shoot(Level world, Player user, InteractionHand hand, boolean leftClick) {
        if (user.isSpectator() || PortalCubedComponents.HOLDER_COMPONENT.get(user).entityBeingHeld() != null) return;
        ItemStack stack = user.getItemInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", !leftClick);
        if (!world.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();

            Portal portalHolder;
            Portal originalPortal;
            CompoundTag portalsTag = tag.getCompound(world.dimension().location().toString());

            if (portalsTag.contains((leftClick ? "Left" : "Right") + "Portal")) {
                originalPortal = (Portal) ((ServerLevel) world).getEntity(portalsTag.getUUID((leftClick ? "Left" : "Right") + "Portal"));
            } else {
                originalPortal = null;
            }
            portalHolder = PortalCubedEntities.PORTAL.create(world);

            Portal otherPortal;
            if (portalsTag.contains((leftClick ? "Right" : "Left") + "Portal")) {
                otherPortal = (Portal) ((ServerLevel) world).getEntity(portalsTag.getUUID((leftClick ? "Right" : "Left") + "Portal"));
            } else {
                otherPortal = null;
            }

            world.playSound(null, user.position().x(), user.position().y(), user.position().z(), leftClick ? PortalCubedSounds.FIRE_EVENT_PRIMARY : PortalCubedSounds.FIRE_EVENT_SECONDARY, SoundSource.NEUTRAL, .3F, 1F);

            Vec3i up;
            Vec3i normal;
            Vec3i right;
            Vec3 blockPos;

            HitResult hitResult = customRaycast(user, 128.0D, 0.0F);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                normal = ((BlockHitResult) hitResult).getDirection().getOpposite().getNormal();
                if (normal.getY() == 0) {
                    up = new Vec3i(0, 1, 0);
                } else {
                    final Vec3 lookAngle = user.getLookAngle();
                    up = Direction.getNearest(lookAngle.x, 0, lookAngle.z).getNormal();
                }
                right = up.cross(normal);

                final int alignment = world.getGameRules().getInt(PortalCubedGameRules.PORTAL_ALIGNMENT);
                if (alignment == 0) {
                    blockPos = hitResult.getLocation();
                } else {
                    blockPos = new Vec3(
                            Math.round(hitResult.getLocation().x * alignment) / (double) alignment,
                            Math.round(hitResult.getLocation().y * alignment) / (double) alignment,
                            Math.round(hitResult.getLocation().z * alignment) / (double) alignment
                    );
                }

                Vec3 portalPos1 = calcPos(blockPos, normal);

                assert portalHolder != null;
                portalHolder.setOwnerUUID(Optional.of(user.getUUID()));
                portalHolder.setOriginPos(portalPos1);
                portalHolder.setDestination(Optional.of(portalPos1));

                portalHolder.setRotation(
                    IPQuaternion.matrixToQuaternion(
                        Vec3.atLowerCornerOf(right),
                        Vec3.atLowerCornerOf(up),
                        Vec3.atLowerCornerOf(normal)
                    ).toQuaternionf()
                );
                portalHolder.setColor(this.getSidedColor(stack));

                //noinspection DataFlowIssue
                final Direction.Axis hAxis = Direction.fromNormal(new BlockPos(right)).getAxis();
                findCorrectOrientation:
                if (!portalHolder.validate()) {
                    for (final var try_ : FAIL_TRIES.get(Pair.of(up, right))) {
                        Vec3 tryPos = portalPos1;
                        for (final Direction part : try_) {
                            double newAxis = FAIL_AXIS_DIRS.get(part.getAxisDirection()).get(tryPos.get(part.getAxis()));
                            if (part.getAxis() == hAxis) {
                                newAxis += part.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -0.5 : 0.5;
                            }
                            tryPos = tryPos.with(part.getAxis(), newAxis);
                        }
                        portalHolder.setOriginPos(tryPos);
                        if (portalHolder.validate()) {
                            break findCorrectOrientation;
                        }
                    }
                    world.playSound(null, user.position().x(), user.position().y(), user.position().z(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundSource.NEUTRAL, 1F, 1F);
                    return;
                }

                final List<Portal> overlappingPortals = world.getEntities(
                        PortalCubedEntities.PORTAL,
                        portalHolder.getBoundingBox(),
                        p -> p != originalPortal && vectorsEqual(p.getNormal(), portalHolder.getNormal())
                );

                if (!overlappingPortals.isEmpty()) {
                    boolean bumpSuccess = false;
                    if (overlappingPortals.size() == 1) {
                        final Portal overlappingPortal = overlappingPortals.get(0);
                        if (overlappingPortal.getAxisW().equals(portalHolder.getAxisW())) {
                            final Direction.Axis axis = Objects.requireNonNull(Direction.fromNormal(new BlockPos(right))).getAxis();
                            if (overlappingPortal.getOriginPos().get(axis) < portalHolder.getOriginPos().get(axis)) {
                                portalHolder.setOriginPos(portalHolder.getOriginPos().with(axis, overlappingPortal.getOriginPos().get(axis) + 1));
                            } else {
                                portalHolder.setOriginPos(portalHolder.getOriginPos().with(axis, overlappingPortal.getOriginPos().get(axis) - 1));
                            }
                            bumpSuccess = portalHolder.validate();
                        }
                    }
                    if (!bumpSuccess) {
                        world.playSound(null, user.position().x(), user.position().y(), user.position().z(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundSource.NEUTRAL, 1F, 1F);
                        return;
                    }
                }

                if (originalPortal == null) {
                    portalHolder.setLinkedPortalUUID(Optional.empty());
                } else {
                    CalledValues.removePortals(user, originalPortal.getUUID());
                    originalPortal.kill();
                }
                world.addFreshEntity(portalHolder);
                CalledValues.addPortals(user, portalHolder.getUUID());
                final boolean isOtherAuto = otherPortal == null;
                if (isOtherAuto) {
                    otherPortal = getPotentialOpposite(
                        world, portalPos1, portalHolder, portalHolder.getColor(), allowLinkingToOther()
                    ).orElse(null);
                }
                if (otherPortal != null) {
                    linkPortals(portalHolder, otherPortal, 0.1f);

                    portalHolder.setOwnerUUID(Optional.of(user.getUUID()));
                    if (!isOtherAuto) {
                        otherPortal.setOwnerUUID(Optional.of(user.getUUID()));
                    }

                    CalledValues.addPortals(user, portalHolder.getUUID());
                    if (!isOtherAuto) {
                        CalledValues.addPortals(user, otherPortal.getUUID());
                    }
                }
            } else {
                world.playSound(null, user.position().x(), user.position().y(), user.position().z(), PortalCubedSounds.INVALID_PORTAL_EVENT, SoundSource.NEUTRAL, 1F, 1F);
                return;
            }

            portalsTag.putUUID((leftClick ? "Left" : "Right") + "Portal", portalHolder.getUUID());

            tag.put(world.dimension().location().toString(), portalsTag);
        } else {
            cancelClientMovement(user);
        }
    }

    /**
     * {@link Vec3#equals} uses {@link Double#compare} to compare axes. {@link Double#compare}, however, treats 0.0 and
     * -0.0 as not equal.
     */
    private static boolean vectorsEqual(Vec3 a, Vec3 b) {
        return a.x() == b.x() && a.y() == b.y() && a.z() == b.z();
    }

    @ClientOnly
    private static void cancelClientMovement(Entity user) {
        if (user instanceof LocalPlayer clientPlayer) {
            if (clientPlayer.input.getMoveVector().lengthSquared() < 0.1 && user.getXRot() >= 88.0) {
                user.setDeltaMovement(0, user.getDeltaMovement().y, 0);
            }
        }
    }

    public static Optional<Portal> getPotentialOpposite(Level world, Vec3 portalPos, @Nullable Portal ignore, int color, boolean includePlayerPortals) {
        return world.getEntities(
            PortalCubedEntities.PORTAL,
            AABB.ofSize(portalPos, 256, 256, 256),
            p ->
                p != ignore &&
                    p.getColor() == 0xffffff - color + 1 &&
                    (includePlayerPortals || p.getOwnerUUID().isEmpty()) &&
                    !p.getActive()
        ).stream().min(Comparator.comparingDouble(p -> p.getOriginPos().distanceToSqr(portalPos)));
    }

    public static void linkPortals(Portal portal1, Portal portal2, float volume) {
        portal1.setDestination(Optional.of(portal2.getOriginPos()));
        portal1.setOtherRotation(Optional.of(portal2.getRotation()));
        portal1.setLinkedPortalUUID(Optional.of(portal2.getUUID()));
        portal2.setDestination(Optional.of(portal1.getOriginPos()));
        portal2.setOtherRotation(Optional.of(portal1.getRotation()));
        portal2.setLinkedPortalUUID(Optional.of(portal1.getUUID()));

        portal1.getLevel().playSound(null, portal1.position().x(), portal1.position().y(), portal1.position().z(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundSource.NEUTRAL, volume, 1F);
        portal2.getLevel().playSound(null, portal2.position().x(), portal2.position().y(), portal2.position().z(), PortalCubedSounds.ENTITY_PORTAL_OPEN, SoundSource.NEUTRAL, volume, 1F);
    }

    /**
     * @param hit     the position designated by the player's input for a given portal.
     * @param facing  the facing axial vector of the portal based on placement context.
     * @return a vector position specifying the portal's final position in the world.
     */
    private Vec3 calcPos(Vec3 hit, Vec3i facing) {
        double faceOffset = -0.01;
        return new Vec3(
            ((hit.x()) + faceOffset * facing.getX()), // x component
            ((hit.y()) + faceOffset * facing.getY()), // y component
            ((hit.z()) + faceOffset * facing.getZ())  // z component
        );
    }

    public HitResult customRaycast(Entity user, double maxDistance, float tickDelta) {
        final Vec3 start = user.getEyePosition(tickDelta);
        final Vec3 rotation = user.getViewVector(tickDelta);
        final Vec3 end = start.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
        final Level world = user.level;
        final CollisionContext shapeContext = CollisionContext.of(user);
        return BlockGetter.traverseBlocks(
            start, end, null,
            (context, pos) -> {
                final BlockState block = world.getBlockState(pos);
                if (block.is(PortalCubedBlocks.PORTAL_NONSOLID)) {
                    return null;
                }
                final VoxelShape blockShape = block.is(PortalCubedBlocks.PORTAL_SOLID)
                    ? block.getShape(world, pos, shapeContext)
                    : block.getCollisionShape(world, pos, shapeContext);
                return world.clipWithInteractionOverride(start, end, pos, blockShape, block);
            },
            context -> {
                final Vec3 offset = start.subtract(end);
                return BlockHitResult.miss(end, Direction.getNearest(offset.x, offset.y, offset.z), BlockPos.containing(end));
            }
        );
    }

}
