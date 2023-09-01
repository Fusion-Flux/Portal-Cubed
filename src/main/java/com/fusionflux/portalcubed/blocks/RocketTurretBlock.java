package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RocketTurretBlock extends BaseEntityBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShape BASE_SHAPE = Shapes.or(
		box(0, 0, 0, 16, 17, 16),
		box(1.97, 16.97, 1.97, 14.03, 18.03, 14.03)
	);
	public static final VoxelShape POWERED_SHAPE = Shapes.or(
		BASE_SHAPE,
		box(4, 16, 4, 12, 33, 12)
	);
	public static final VoxelShape POWERED_COLLISION_SHAPE = Shapes.or(
		BASE_SHAPE,
		box(4, 16, 4, 12, 32, 12)
	);

	public RocketTurretBlock(Properties settings) {
		super(settings);
		registerDefaultState(
			getStateDefinition().any()
				.setValue(POWERED, false)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(POWERED) ? POWERED_SHAPE : BASE_SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(POWERED) ? POWERED_COLLISION_SHAPE : BASE_SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RocketTurretBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		final boolean powered = world.hasNeighborSignal(pos);
		if (!defaultBlockState().is(block) && powered != state.getValue(POWERED)) {
			world.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return type == PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY
			? (world1, pos, state1, entity) -> ((RocketTurretBlockEntity)entity).tick(world1, pos, state1)
			: null;
	}
}
