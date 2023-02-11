package com.fusionflux.portalcubed.client.render.block;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public final class CullingCache {

	private final BlockPos.Mutable pos = new BlockPos.Mutable();

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

	public boolean shouldCull(QuadView quad, BlockRenderView blockView) {
		Direction cullFace = quad.cullFace();
		if (cullFace == null) return false;
		int mask = 1 << cullFace.ordinal();

		if ((completionFlags & mask) == 0) {
			completionFlags |= mask;

			boolean shouldCull = !Block.shouldDrawSide(state, blockView, centerPos, cullFace, pos.set(centerPos, cullFace));
			if (shouldCull) values |= mask;
			return shouldCull;
		} else {
			return (values & mask) == 0;
		}
	}

}
