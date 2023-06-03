package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class FizzlerEmitter extends HorizontalDirectionalBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final AbstractFizzlerBlock fizzlerBlock;

    public FizzlerEmitter(Properties settings, AbstractFizzlerBlock fizzlerBlock) {
        super(settings);
        this.fizzlerBlock = fizzlerBlock;
        registerDefaultState(
            getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        final DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
        return neighborState.is(this) && neighborState.getValue(HALF) != half
            ? state.setValue(FACING, neighborState.getValue(FACING))
                .setValue(POWERED, neighborState.getValue(POWERED))
            : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(POWERED)) {
            updateGrill(world, pos.immutable(), state, false);
            updateGrill(world, state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above(), state.cycle(HALF), false);
        }
        if (!world.isClientSide && player.isCreative()) {
            onBreakInCreative(world, pos, state, player);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, 35);
                world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockPos pos = ctx.getClickedPos();
        final Level world = ctx.getLevel();
        if (pos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(pos.above()).canBeReplaced(ctx)) {
            return defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(POWERED, world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above()));
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        final BlockPos otherPos = pos.above();
        final BlockState otherState = state.setValue(HALF, DoubleBlockHalf.UPPER);
        world.setBlockAndUpdate(otherPos, otherState);
        if (state.getValue(POWERED)) {
            updateGrill(world, pos, state, true);
            updateGrill(world, otherPos, otherState, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final BlockPos otherPos = pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        final boolean powered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(otherPos);
        if (!defaultBlockState().is(block) && powered != state.getValue(POWERED)) {
            updateGrill(world, pos, state, powered);
            updateGrill(world, otherPos, state.cycle(HALF), powered);
            world.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        }
    }

    private void updateGrill(Level world, BlockPos pos, BlockState state, boolean placed) {
        if (world.isClientSide) return;
        final Direction searchDir = state.getValue(FACING);
        final BooleanProperty grillAxis = AbstractFizzlerBlock.getStateForAxis(searchDir.getAxis());
        final BlockState targetState = state.setValue(FACING, searchDir.getOpposite()).setValue(POWERED, placed);
        BlockPos searchPos = pos.relative(searchDir);
        int i;
        for (i = 0; i < PortalCubedConfig.maxBridgeLength; i++) {
            final BlockState checkState = world.getBlockState(searchPos);
            if (checkState.equals(targetState)) break;
            if (placed && !checkState.isAir() && !checkState.is(fizzlerBlock)) return;
            if (!placed && checkState.is(fizzlerBlock)) {
                final BlockState newState = checkState.setValue(grillAxis, false);
                world.setBlockAndUpdate(searchPos, AbstractFizzlerBlock.isEmpty(newState) ? Blocks.AIR.defaultBlockState() : newState);
            }
            searchPos = searchPos.relative(searchDir);
        }
        if (!placed || i == PortalCubedConfig.maxBridgeLength) return;
        final BlockState placedState = fizzlerBlock.defaultBlockState()
            .setValue(grillAxis, true)
            .setValue(HALF, state.getValue(HALF));
        searchPos = pos.relative(searchDir);
        for (i = 0; i < PortalCubedConfig.maxBridgeLength; i++) {
            final BlockState checkState = world.getBlockState(searchPos);
            if (checkState.equals(targetState)) break;
            world.setBlockAndUpdate(searchPos, checkState.is(Blocks.AIR) ? placedState : checkState.setValue(grillAxis, true));
            searchPos = searchPos.relative(searchDir);
        }
    }
}
