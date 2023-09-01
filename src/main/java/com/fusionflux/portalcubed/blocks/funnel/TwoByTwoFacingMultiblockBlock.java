package com.fusionflux.portalcubed.blocks.funnel;

import com.fusionflux.portalcubed.util.TwoByTwo;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.HashMap;
import java.util.Map;

public interface TwoByTwoFacingMultiblockBlock {
	IntegerProperty QUADRANT = IntegerProperty.create("quadrant", 1, 4);
	DirectionProperty FACING = BlockStateProperties.FACING;

	// oh the misery
	@SuppressWarnings("unchecked")
	Map<Direction, Direction>[] QUADRANT_INDEX_FACING_TO_NEXT = Util.make(new Map[4], (Map<Direction, Direction>[] array) -> {
		array[0] = Map.of(
				Direction.NORTH, Direction.EAST,
				Direction.EAST, Direction.SOUTH,
				Direction.SOUTH, Direction.WEST,
				Direction.WEST, Direction.NORTH,
				Direction.UP, Direction.EAST,
				Direction.DOWN, Direction.EAST
		);
		array[1] = Map.of(
				Direction.NORTH, Direction.DOWN,
				Direction.EAST, Direction.DOWN,
				Direction.SOUTH, Direction.DOWN,
				Direction.WEST, Direction.DOWN,
				Direction.UP, Direction.NORTH,
				Direction.DOWN, Direction.SOUTH
		);

		Map<Direction, Direction> three = new HashMap<>();
		array[0].forEach((facing, next) -> three.put(facing, next.getOpposite()));
		array[2] = three;

		Map<Direction, Direction> four = new HashMap<>();
		array[1].forEach((facing, next) -> four.put(facing, next.getOpposite()));
		array[3] = four;
	});

	static Direction directionToNextQuadrant(int quadrant, Direction facing) {
		return QUADRANT_INDEX_FACING_TO_NEXT[quadrant - 1].get(facing);
	}

	static Direction directionToPreviousQuadrant(int quadrant, Direction facing) {
		int prevQuadrant = quadrant == 1 ? 4 : quadrant - 1;
		return QUADRANT_INDEX_FACING_TO_NEXT[prevQuadrant - 1].get(facing).getOpposite();
	}

	static TwoByTwo makeMultiblockFromQuadrant(BlockPos pos, int quadrant, Direction facing) {
		if (quadrant < 1 || quadrant > 4)
			throw new IllegalArgumentException("Invalid quadrant: " + quadrant);
		if (quadrant == 1) {
			Direction toNext = directionToNextQuadrant(quadrant, facing);
			pos = pos.relative(toNext);
			quadrant = 2;
		} else if (quadrant == 3) {
			Direction toNext = directionToNextQuadrant(quadrant, facing);
			pos = pos.relative(toNext);
			quadrant = 4;
		}
		if (quadrant == 2) {
			Direction right = directionToPreviousQuadrant(2, facing);
			Direction down = directionToNextQuadrant(2, facing);
			return TwoByTwo.fromTopLeftCorner(pos, right, down);
		} else { // quadrant == 4
			Direction left = directionToPreviousQuadrant(4, facing);
			Direction up = directionToNextQuadrant(4, facing);
			return TwoByTwo.fromBottomRightCorner(pos, left, up);
		}
	}
}
