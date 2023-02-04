package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FizzlerEmitter extends HorizontalFacingBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = Properties.POWERED;

    private final AbstractFizzlerBlock fizzlerBlock;

    public FizzlerEmitter(Settings settings, AbstractFizzlerBlock fizzlerBlock) {
        super(settings);
        this.fizzlerBlock = fizzlerBlock;
        setDefaultState(
            getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(POWERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        final DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        return neighborState.isOf(this) && neighborState.get(HALF) != half
            ? state.with(FACING, neighborState.get(FACING))
                .with(POWERED, neighborState.get(POWERED))
            : Blocks.AIR.getDefaultState();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.get(POWERED)) {
            updateGrill(world, pos.toImmutable(), state, false);
            updateGrill(world, state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up(), state.cycle(HALF), false);
        }
        if (!world.isClient && player.isCreative()) {
            onBreakInCreative(world, pos, state, player);
        }

        super.onBreak(world, pos, state, player);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)
                    ? Blocks.WATER.getDefaultState()
                    : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState2, 35);
                world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final BlockPos pos = ctx.getBlockPos();
        final World world = ctx.getWorld();
        if (pos.getY() < world.getTopY() - 1 && world.getBlockState(pos.up()).canReplace(ctx)) {
            return getDefaultState()
                .with(FACING, ctx.getPlayerFacing().getOpposite())
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(POWERED, world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up()));
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        final BlockPos otherPos = pos.up();
        final BlockState otherState = state.with(HALF, DoubleBlockHalf.UPPER);
        world.setBlockState(otherPos, otherState);
        if (state.get(POWERED)) {
            updateGrill(world, pos, state, true);
            updateGrill(world, otherPos, otherState, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final BlockPos otherPos = pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        final boolean powered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(otherPos);
        if (!getDefaultState().isOf(block) && powered != state.get(POWERED)) {
            updateGrill(world, pos, state, powered);
            updateGrill(world, otherPos, state.cycle(HALF), powered);
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);
        }
    }

    private void updateGrill(World world, BlockPos pos, BlockState state, boolean placed) {
        if (world.isClient) return;
        final Direction searchDir = state.get(FACING);
        final BooleanProperty grillAxis = AbstractFizzlerBlock.getStateForAxis(searchDir.getAxis());
        final BlockState targetState = state.with(FACING, searchDir.getOpposite()).with(POWERED, placed);
        BlockPos searchPos = pos.offset(searchDir);
        int i;
        for (i = 0; i < PortalCubedConfig.maxBridgeLength; i++) {
            final BlockState checkState = world.getBlockState(searchPos);
            if (checkState.equals(targetState)) break;
            if (placed && !checkState.isAir() && !checkState.isOf(fizzlerBlock)) return;
            if (!placed && checkState.isOf(fizzlerBlock)) {
                final BlockState newState = checkState.with(grillAxis, false);
                world.setBlockState(searchPos, AbstractFizzlerBlock.isEmpty(newState) ? Blocks.AIR.getDefaultState() : newState);
            }
            searchPos = searchPos.offset(searchDir);
        }
        if (!placed || i == PortalCubedConfig.maxBridgeLength) return;
        final BlockState placedState = fizzlerBlock.getDefaultState()
            .with(grillAxis, true)
            .with(HALF, state.get(HALF));
        searchPos = pos.offset(searchDir);
        for (i = 0; i < PortalCubedConfig.maxBridgeLength; i++) {
            final BlockState checkState = world.getBlockState(searchPos);
            if (checkState.equals(targetState)) break;
            world.setBlockState(searchPos, checkState.isOf(Blocks.AIR) ? placedState : checkState.with(grillAxis, true));
            searchPos = searchPos.offset(searchDir);
        }
    }
}
