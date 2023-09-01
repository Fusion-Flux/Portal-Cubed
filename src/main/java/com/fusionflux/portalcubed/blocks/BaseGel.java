package com.fusionflux.portalcubed.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

public class BaseGel extends SimpleMultiSidedBlock {
	public BaseGel(Properties settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (world.isRainingAt(pos.above())) {
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}

	public static boolean collides(Entity entity, BlockPos pos, BlockState state) {
		return Shapes.joinIsNotEmpty(
			state.getShape(entity.level(), pos, CollisionContext.of(entity)).move(pos.getX(), pos.getY(), pos.getZ()),
			Shapes.create(entity.getBoundingBox()),
			BooleanOp.AND
		);
	}
}
