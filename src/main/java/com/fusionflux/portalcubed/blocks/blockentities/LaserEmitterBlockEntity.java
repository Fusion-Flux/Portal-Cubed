package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.LaserCatcherBlock;
import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.GeneralUtil;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LaserEmitterBlockEntity extends BlockEntity {
    private record Target(@NotNull BlockPos pos, @Nullable Direction side) {
    }

    private final List<AdvancedEntityRaycast.Result> multiSegments = new ArrayList<>();
    private final Set<Target> targets = new HashSet<>();

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        final NbtList list = new NbtList();
        for (final Target target : targets) {
            final NbtCompound targetNbt = new NbtCompound();
            targetNbt.putIntArray("Target", new int[] {target.pos.getX(), target.pos.getY(), target.pos.getZ()});
            if (target.side != null) {
                targetNbt.putString("TargetSide", target.side.getName());
            }
            list.add(targetNbt);
        }
        tag.put("Targets", list);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        targets.clear();
        for (final NbtElement elem : tag.getList("Targets", NbtElement.COMPOUND_TYPE)) {
            final NbtCompound targetNbt = (NbtCompound)elem;
            final int[] targetA = targetNbt.getIntArray("Target");
            if (targetA.length >= 3) {
                targets.add(new Target(
                    new BlockPos(targetA[0], targetA[1], targetA[2]),
                    Direction.byName(tag.getString("TargetSide"))
                ));
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        multiSegments.clear();
        if (!state.get(LaserEmitterBlock.POWERED)) {
            if (!world.isClient) {
                for (final Target target : targets) {
                    world.getBlockEntity(target.pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::removeLaser);
                }
                targets.clear();
            }
            return;
        }
        Vec3d direction = Vec3d.of(state.get(LaserEmitterBlock.FACING).getVector());
        Vec3d start = Vec3d.ofCenter(pos).add(direction.multiply(0.5));
        double lengthRemaining = PortalCubedConfig.maxBridgeLength;
        final Set<Entity> alreadyHit = new HashSet<>();
        AdvancedEntityRaycast.Result segments;
        do {
            //noinspection DataFlowIssue
            segments = AdvancedEntityRaycast.raycast(
                world,
                new RaycastContext(
                    start, start.add(direction.multiply(lengthRemaining)),
                    RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, null
                ),
                PortalDirectionUtils.PORTAL_RAYCAST_TRANSFORM,
                new AdvancedEntityRaycast.TransformInfo(
                    e -> e instanceof RedirectionCubeEntity && !alreadyHit.contains(e),
                    (context, blockHit, entityHit) -> {
                        final var entity = (RedirectionCubeEntity)entityHit.getEntity();
                        alreadyHit.add(entity);
                        final double distance = context.getStart().distanceTo(context.getEnd());
                        final Vec3d offset = entityHit.getPos().subtract(context.getStart());
                        final Vec3d newOffset = Vec3d.fromPolar(entity.getPitch(), entity.getYaw())
                            .multiply(distance - offset.length());
                        final Vec3d origin = entity.getPos().add(new Vec3d(0, entity.getHeight() / 2, 0));
                        return new AdvancedEntityRaycast.TransformResult(
                            entityHit.getPos().add(offset.multiply(0.25 / offset.length())),
                            AdvancedEntityRaycast.withStartEnd(context, origin, origin.add(newOffset))
                        );
                    }
                )
            );
            direction = segments.finalRay().relative().normalize();
            start = segments.finalRay().end();
            lengthRemaining -= segments.length();
            multiSegments.add(segments);
        } while (
            segments.finalHit().getType() == HitResult.Type.BLOCK &&
                world.getBlockState(segments.finalHit().getBlockPos()).isOf(PortalCubedBlocks.LASER_RELAY)
        );
        if (world.isClient) return;

        Entity owner = EntityType.MARKER.create(world);
        assert owner != null;
        for (final AdvancedEntityRaycast.Result result : multiSegments) {
            for (final AdvancedEntityRaycast.Result.Ray ray : result.rays()) {
                Vec3d rayStart = ray.start();
                EntityHitResult hit;
                do {
                    hit = new AdvancedEntityRaycast.Result.Ray(rayStart, ray.end(), null).entityRaycast(owner, e -> e instanceof LivingEntity);
                    if (hit != null) {
                        rayStart = hit.getPos();
                        final Entity hitEntity = hit.getEntity();
                        owner = hitEntity;
                        if (hitEntity instanceof CorePhysicsEntity) {
                            continue; // TODO: Turrets and chairs burn
                        }
                        hitEntity.damage(DamageSource.IN_FIRE, 5);
                        final Vec3d velocity = GeneralUtil.calculatePerpendicularVector(ray.start(), ray.end(), hitEntity.getPos())
                            .normalize()
                            .multiply(hitEntity.isOnGround() ? 1.25 : 0.25);
                        hitEntity.addVelocity(velocity.x, velocity.y, velocity.z);
                    }
                } while (hit != null && !GeneralUtil.targetsEqual(hit, ray.hit()));
                if (ray.hit() instanceof EntityHitResult entityHitResult) {
                    owner = entityHitResult.getEntity();
                }
            }
        }

        final Set<Target> newTargets = new HashSet<>();
        final Object2IntMap<BlockPos> changes = new Object2IntOpenHashMap<>(targets.size() + multiSegments.size());
        for (final AdvancedEntityRaycast.Result result : multiSegments) {
            final BlockHitResult finalHit = result.finalHit();
            if (finalHit.getType() == HitResult.Type.MISS) continue;
            final BlockState hitState = world.getBlockState(finalHit.getBlockPos());
            if (hitState.isOf(PortalCubedBlocks.LASER_CATCHER) && finalHit.getSide() != hitState.get(LaserCatcherBlock.FACING)) continue;
            final Target target = new Target(
                finalHit.getBlockPos(),
                hitState.isOf(PortalCubedBlocks.LASER_RELAY) ? null : finalHit.getSide()
            );
            newTargets.add(target);
            if (targets.add(target)) {
                changes.put(target.pos, changes.getOrDefault(target.pos, 0) + 1);
            }
        }

        for (final Target target : targets) {
            if (!newTargets.contains(target)) {
                changes.put(target.pos, changes.getOrDefault(target.pos, 0) - 1);
            }
        }
        targets.retainAll(newTargets);

        for (final var entry : changes.object2IntEntrySet()) {
            if (entry.getIntValue() == 0) continue;
            final LaserNodeBlockEntity entity = world.getBlockEntity(entry.getKey(), PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).orElse(null);
            if (entity == null) continue;
            if (entry.getIntValue() > 0) {
                for (int i = 0; i < entry.getIntValue(); i++) {
                    entity.addLaser();
                }
            } else {
                for (int i = 0; i < -entry.getIntValue(); i++) {
                    entity.removeLaser();
                }
            }
        }
        if (!changes.isEmpty()) {
            markDirty();
        }
    }

    public List<AdvancedEntityRaycast.Result> getMultiSegments() {
        return multiSegments;
    }
}
