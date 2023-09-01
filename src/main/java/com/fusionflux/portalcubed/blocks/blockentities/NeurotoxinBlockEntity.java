package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NeurotoxinBlockEntity extends BlockEntity {
	private int age = 1;

	public NeurotoxinBlockEntity(BlockPos pos, BlockState state) {
		super(PortalCubedBlocks.NEUROTOXIN_BLOCK_ENTITY, pos, state);
	}

	public static void tick(Level world, BlockPos pos, @SuppressWarnings("unused") BlockState state, NeurotoxinBlockEntity blockEntity) {
		assert world != null;

		if (!world.isClientSide) {
			blockEntity.age++;
		}

		if (!world.isClientSide && blockEntity.age % 5 == 0) {
			for (int i = 0; i < 3; i++) {
				Direction dir = Direction.getRandom(world.getRandom());
				if (world.getBlockState(blockEntity.getBlockPos().relative(dir)).isAir()) {
					world.setBlockAndUpdate(blockEntity.getBlockPos().relative(dir), blockEntity.getBlockState());
					world.setBlockAndUpdate(blockEntity.getBlockPos(), Blocks.AIR.defaultBlockState());
					if (world.getBlockEntity(pos.relative(dir)) instanceof NeurotoxinBlockEntity newBE) {
						newBE.age = blockEntity.age;
					}
					break;
				}
			}
		}

		if (world.random.nextInt(blockEntity.age) > 100)
			world.setBlockAndUpdate(blockEntity.getBlockPos(), Blocks.AIR.defaultBlockState());
	}
}
