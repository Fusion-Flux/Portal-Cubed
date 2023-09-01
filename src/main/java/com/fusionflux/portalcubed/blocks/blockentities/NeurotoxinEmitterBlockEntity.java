package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class NeurotoxinEmitterBlockEntity extends BlockEntity {

	private final BlockPos.MutableBlockPos obstructorPos;

	public NeurotoxinEmitterBlockEntity(BlockPos pos, BlockState state) {
		super(PortalCubedBlocks.NEUROTOXIN_EMITTER_ENTITY, pos, state);
		this.obstructorPos = pos.mutable();
	}



	public static void tick(Level world, BlockPos pos, @SuppressWarnings("unused") BlockState state, NeurotoxinEmitterBlockEntity blockEntity) {
		assert world != null;
		if (!world.isClientSide) {
			boolean redstonePowered = world.hasNeighborSignal(blockEntity.getBlockPos());

			if (redstonePowered) {
				// Update blockstate
				if (!world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
					blockEntity.togglePowered(world.getBlockState(pos));
				}
			}
			if (!redstonePowered) {
				// Update blockstate
				if (world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
					blockEntity.togglePowered(world.getBlockState(pos));
				}
			}
			if (world.isEmptyBlock(blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(BlockStateProperties.FACING))) && world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
				world.setBlockAndUpdate(blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(BlockStateProperties.FACING)), PortalCubedBlocks.NEUROTOXIN_BLOCK.defaultBlockState());
			}

		}
	}

	public void spookyUpdateObstructor(BlockPos ownerPos) {
		this.obstructorPos.set(ownerPos);
	}

	private void togglePowered(BlockState state) {
		assert level != null;
		level.setBlockAndUpdate(worldPosition, state.cycle(BlockStateProperties.POWERED));
	}

}
