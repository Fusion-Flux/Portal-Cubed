package com.fusionflux.portalcubed.blocks.bridge;

import java.util.HashMap;
import java.util.Map;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.VoxelShaper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HardLightBridgeEmitterBlock extends Block implements HardLightBridgePart {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final VoxelShape BASE_SHAPE_DOWN = Block.box(0, 0, 0, 16, 4, 4);
    public static final VoxelShape BASE_SHAPE_RIGHT = Block.box(0, 0, 0, 4, 16, 4);
    public static final VoxelShape BASE_SHAPE_UP = Block.box(0, 12, 0, 16, 16, 4);
    public static final VoxelShape BASE_SHAPE_LEFT = Block.box(12, 0, 0, 16, 16, 4);

    public static final Map<Edge, VoxelShaper> SHAPERS = Util.make(new HashMap<>(), map -> {
        map.put(Edge.DOWN, VoxelShaper.forDirectional(BASE_SHAPE_DOWN, Direction.SOUTH));
        map.put(Edge.RIGHT, VoxelShaper.forDirectional(BASE_SHAPE_RIGHT, Direction.SOUTH));
        map.put(Edge.UP, VoxelShaper.forDirectional(BASE_SHAPE_UP, Direction.SOUTH));
        map.put(Edge.LEFT, VoxelShaper.forDirectional(BASE_SHAPE_LEFT, Direction.SOUTH));
    });

    private static final Map<BlockState, VoxelShape> shapes = new HashMap<>();

    static boolean suppressUpdates;

    public HardLightBridgeEmitterBlock(Properties settings) {
        super(settings);
        registerDefaultState(
                stateDefinition.any()
                        .setValue(EDGE, Edge.DOWN)
                        .setValue(FACING, Direction.SOUTH)
                        .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EDGE, FACING, POWERED);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).is(PortalCubedItems.HAMMER))
            return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;
        Edge edge = state.getValue(EDGE).getClockwise();
        BlockState newState = state.setValue(EDGE, edge);
        level.setBlockAndUpdate(pos, newState);
        updateEmission(serverLevel, newState, pos, newState.getValue(POWERED));
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getNearestLookingDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(level instanceof ServerLevel serverLevel) || HardLightBridgeEmitterBlock.suppressUpdates)
            return;
        boolean update = false;
        Direction facing = state.getValue(FACING);
        if (pos.relative(facing).equals(fromPos)) { // pos in front
            BlockState newState = level.getBlockState(fromPos);
            if (newState.isAir()) // removed, try to extend
                update = true;
        }

        boolean powered = level.hasNeighborSignal(pos);
        update |= powered != state.getValue(POWERED);
        if (!update)
            return;
        BlockState newState = state.setValue(POWERED, powered);
        level.setBlockAndUpdate(pos, newState);
        updateEmission(serverLevel, newState, pos, powered);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel && !HardLightBridgeEmitterBlock.suppressUpdates && state.getValue(POWERED))
            HardLightBridgeEmitterBlock.updateEmission(serverLevel, state, pos, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return shapes.computeIfAbsent(state, HardLightBridgeEmitterBlock::makeShape);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.is(this) || stateFrom.is(PortalCubedBlocks.HLB_BLOCK)) {
            return stateFrom.getValue(FACING) == state.getValue(FACING);
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public static void updateEmission(ServerLevel level, BlockState emitterState, BlockPos emitterPos, boolean powered) {
        withUpdatesSuppressed(() -> {
            Direction facing = emitterState.getValue(FACING);
            Edge edge = emitterState.getValue(EDGE);
            BlockState bridgeState = !powered ? Blocks.AIR.defaultBlockState()
                    : PortalCubedBlocks.HLB_BLOCK.defaultBlockState()
                    .setValue(FACING, facing).setValue(EDGE, edge);

            MutableBlockPos pos = emitterPos.mutable();
            for (int i = 0; i < PortalCubedConfig.maxBridgeLength; i++) {
                pos.move(facing);
                if (canPlaceBridge(level, pos, facing)) {
                    level.setBlockAndUpdate(pos, bridgeState);
                } else {
                    // TODO check for portals
                    break;
                }
            }
        });
    }

    public static void withUpdatesSuppressed(Runnable runnable) {
        try {
            suppressUpdates = true;
            runnable.run();
        } finally {
            suppressUpdates = false;
        }
    }

    private static boolean canPlaceBridge(ServerLevel level, BlockPos pos, Direction facing) {
        if (!level.isLoaded(pos))
            return false;
        BlockState state = level.getBlockState(pos);
        if (!state.is(PortalCubedBlocks.HLB_BLOCK))
            return state.isAir();
        return state.getValue(FACING) == facing;
    }

    private static VoxelShape makeShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        Edge edge = state.getValue(EDGE);
        if (facing.getAxis().isVertical())
            edge = edge.getOpposite();
        VoxelShape shape = SHAPERS.get(edge).get(facing);
        if (state.getValue(POWERED)) {
            shape = Shapes.or(shape, HardLightBridgeBlock.makeShape(state));
        }
        return shape;
    }
}
