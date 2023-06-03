package com.fusionflux.portalcubed.client.render.block;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class CullingCache {

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private BlockPos centerPos;
    private BlockState state;

    private int completionFlags = 0;
    private int values = 0;

    public void prepare(BlockPos centerPos, BlockState state) {
        this.centerPos = centerPos;
        this.state = state;

        this.completionFlags = 0;
        this.values = 0;
    }

    public boolean shouldCull(QuadView quad, BlockAndTintGetter blockView) {
        Direction cullFace = quad.cullFace();
        if (cullFace == null) return false;
        int mask = 1 << cullFace.ordinal();

        if ((completionFlags & mask) == 0) {
            completionFlags |= mask;

            boolean shouldCull = !Block.shouldRenderFace(state, blockView, centerPos, cullFace, pos.setWithOffset(centerPos, cullFace));
            if (shouldCull) values |= mask;
            return shouldCull;
        } else {
            return (values & mask) == 0;
        }
    }

}
