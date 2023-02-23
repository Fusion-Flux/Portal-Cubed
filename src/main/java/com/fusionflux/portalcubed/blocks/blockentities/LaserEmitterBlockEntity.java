package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaserEmitterBlockEntity extends BlockEntity {
    @Nullable
    private AdvancedEntityRaycast.Result segments;

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
    }

    @Override
    public void readNbt(NbtCompound tag) {
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!state.get(LaserEmitterBlock.POWERED)) {
            segments = null;
            return;
        }
        final Vec3d direction = Vec3d.of(state.get(LaserEmitterBlock.FACING).getVector());
        final Vec3d start = Vec3d.ofCenter(pos).add(direction.multiply(0.5));
        //noinspection DataFlowIssue
        segments = AdvancedEntityRaycast.raycast(
            world,
            new RaycastContext(
                start, start.add(direction.multiply(PortalCubedConfig.maxBridgeLength)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, null
            ),
            PortalDirectionUtils.PORTAL_RAYCAST_TRANSFORM,
            new AdvancedEntityRaycast.TransformInfo(
                e -> e instanceof RedirectionCubeEntity,
                (context, blockHit, entityHit) -> {
                    final var entity = (RedirectionCubeEntity)entityHit.getEntity();
                    final double distance = context.getStart().distanceTo(context.getEnd());
                    final Vec3d offset = entityHit.getPos().subtract(context.getStart());
                    final Vec3d newOffset = Vec3d.fromPolar(entity.getPitch(), entity.getYaw())
                        .multiply(distance - offset.length());
                    final Vec3d origin = entity.getPos().add(new Vec3d(0, entity.getHeight() / 2, 0));
                    return new Pair<>(
                        entityHit.getPos().add(offset.multiply(0.25 / offset.length())),
                        AdvancedEntityRaycast.withStartEnd(context, origin, origin.add(newOffset))
                    );
                }
            )
        );
    }

    @Nullable
    public AdvancedEntityRaycast.Result getSegments() {
        return segments;
    }
}
