package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LaserRelayBlock extends AbstractLaserNodeBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	private static final VoxelShape UP_SHAPE = Shapes.or(
		box(4, 0, 4, 12, 11, 12)
	);
	private static final VoxelShape DOWN_SHAPE = Shapes.or(
		box(4, 6, 4, 12, 16, 12)
	);
	private static final VoxelShape NORTH_SHAPE = Shapes.or(
		box(4, 4, 6, 12, 12, 16)
	);
	private static final Map<Direction, VoxelShape> DIRECTION_TO_SHAPE = Direction.stream()
		.filter(d -> d.getAxis().isHorizontal())
		.collect(Collectors.toMap(
			Function.identity(),
			d -> GeneralUtil.rotate(NORTH_SHAPE, d),
			(m1, m2) -> {
				throw new AssertionError("Sequential stream");
			},
			() -> new EnumMap<>(Direction.class)
		));

	static {
		DIRECTION_TO_SHAPE.put(Direction.UP, UP_SHAPE);
		DIRECTION_TO_SHAPE.put(Direction.DOWN, DOWN_SHAPE);
	}

	public LaserRelayBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, false));
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return DIRECTION_TO_SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ENABLED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return PortalCubedBlocks.LASER_RELAY.defaultBlockState().setValue(FACING, ctx.getClickedFace());
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
}
