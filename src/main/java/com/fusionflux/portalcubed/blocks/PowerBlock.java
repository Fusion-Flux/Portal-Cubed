package com.fusionflux.portalcubed.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PowerBlock extends SpecialHiddenBlock {
	public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;

	public static final VoxelShape SHAPE = box(2, 2, 2, 14, 14, 14);

	public PowerBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(LEVEL, 15));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LEVEL);
	}

	@Override
	protected VoxelShape getVisibleOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			world.setBlock(pos, state.cycle(LEVEL), Block.UPDATE_ALL);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		final ItemStack stack = super.getCloneItemStack(world, pos, state);
		if (state.getValue(LEVEL) != 15) {
			final CompoundTag compound = new CompoundTag();
			compound.putString(LEVEL.getName(), Integer.toString(state.getValue(LEVEL)));
			stack.addTagElement("BlockStateTag", compound);
		}
		return stack;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(LEVEL);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(LEVEL);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isSignalSource(BlockState state) {
		return state.getValue(LEVEL) > 0;
	}
}
