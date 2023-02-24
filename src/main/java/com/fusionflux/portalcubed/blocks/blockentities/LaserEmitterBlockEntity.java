package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.LaserCatcherBlock;
import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LaserEmitterBlockEntity extends BlockEntity {
    @Nullable
    private AdvancedEntityRaycast.Result segments;

    @Nullable
    private BlockPos target;
    @Nullable
    private Direction targetSide;

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        if (target != null) {
            assert targetSide != null;
            tag.putIntArray("Target", new int[] {target.getX(), target.getY(), target.getZ()});
            tag.putString("TargetSide", targetSide.getName());
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        final int[] targetA = tag.getIntArray("Target");
        if (targetA.length >= 3) {
            target = new BlockPos(targetA[0], targetA[1], targetA[2]);
            targetSide = Direction.byName(tag.getString("TargetSide"));
        } else {
            target = null;
            targetSide = null;
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!state.get(LaserEmitterBlock.POWERED)) {
            segments = null;
            if (target != null) {
                world.getBlockEntity(target, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::removeLaser);
                target = null;
                targetSide = null;
            }
            return;
        }
        final Vec3d direction = Vec3d.of(state.get(LaserEmitterBlock.FACING).getVector());
        final Vec3d start = Vec3d.ofCenter(pos).add(direction.multiply(0.5));
        final Set<Entity> alreadyHit = new HashSet<>();
        //noinspection DataFlowIssue
        segments = AdvancedEntityRaycast.raycast(
            world,
            new RaycastContext(
                start, start.add(direction.multiply(PortalCubedConfig.maxBridgeLength)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, null
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
        if (world.isClient) return;
        final BlockHitResult finalHit = segments.finalHit();
        if (finalHit.getType() == HitResult.Type.MISS) {
            if (target != null) {
                world.getBlockEntity(target, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::removeLaser);
                target = null;
                targetSide = null;
            }
            return;
        }
        final BlockState targetState = world.getBlockState(finalHit.getBlockPos());
        final boolean singleSide = targetState.isOf(PortalCubedBlocks.LASER_CATCHER);
        if (!singleSide && finalHit.getBlockPos().equals(target)) return;
        if (finalHit.getSide() == targetSide && finalHit.getBlockPos().equals(target)) return;
        if (target != null) {
            world.getBlockEntity(target, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::removeLaser);
            target = null;
            targetSide = null;
        }
        if (singleSide && finalHit.getSide() != targetState.get(LaserCatcherBlock.FACING)) return;
        world.getBlockEntity(finalHit.getBlockPos(), PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::addLaser);
        target = finalHit.getBlockPos();
        targetSide = finalHit.getSide();
    }

    @Nullable
    public AdvancedEntityRaycast.Result getSegments() {
        return segments;
    }
}
