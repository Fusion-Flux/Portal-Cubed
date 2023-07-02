package com.fusionflux.portalcubed.blocks.bridge;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.VoxelShaper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

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
        if (!player.getItemInHand(hand).is(PortalCubedItems.WRENCHES))
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
        Edge edge = Edge.DOWN;
        if (facing.getAxis().isHorizontal()) {
            if (ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5)
                edge = Edge.UP;
        } else {
            Direction edgeSide = ctx.getHorizontalDirection();
            if (facing == Direction.UP)
                edgeSide = edgeSide.getOpposite();
            edge = Edge.fromFacingAndSide(facing, edgeSide);
        }
        return defaultBlockState().setValue(FACING, facing).setValue(EDGE, edge);
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
        return SHAPES.computeIfAbsent(state, HardLightBridgeEmitterBlock::makeShape);
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
                } else { // hit a wall, check for portals
                    pos.move(facing.getOpposite()); // step back
                    AABB box = new AABB(pos);
                    List<Portal> portals = level.getEntitiesOfClass(Portal.class, box);
                    for (Portal portal : portals) {
                        Direction portalFacing = portal.getFacingDirection();
                        if (portalFacing.getOpposite() != facing)
                            continue;
                        Portal linked = portal.findLinkedPortal();
                        if (linked == null)
                            continue;
                        if (!portal.snapToGrid() || !linked.snapToGrid())
                            continue;
                        Vec3 bridgePos = edge.offsetTowards(Vec3.atCenterOf(pos), facing, 0.4);
                        Vec3 relative = bridgePos.subtract(portal.getOriginPos());
                        Axis xAxis = Edge.RIGHT.toDirection(facing).getAxis();
                        double x = relative.get(xAxis);
                        Axis yAxis = Edge.UP.toDirection(facing).getAxis();
                        double y = relative.get(yAxis);
                        Vec3 linkedBase = linked.getPointInPlane(-x, -y); // negative???
                        Vec3 linkedBridgePos = linkedBase.relative(linked.getFacingDirection(), 0.5);

                        // successfully teleported. update stuff and continue propagating.
                        pos.set(linkedBridgePos.x, linkedBridgePos.y, linkedBridgePos.z);
                        edge = Edge.teleport(edge, portal, facing, linked.getFacingDirection());
                        facing = linked.getFacingDirection();

                        pos.move(facing.getOpposite()); // step back, will move forward again on next loop
                        bridgeState = !powered ? Blocks.AIR.defaultBlockState()
                                : PortalCubedBlocks.HLB_BLOCK.defaultBlockState()
                                .setValue(FACING, facing).setValue(EDGE, edge);
                        break;
                    }
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

    @Override
    public void onPortalCreate(ServerLevel level, BlockState state, BlockPos pos, Portal portal) {
        Direction facing = state.getValue(FACING);
        if (facing != portal.getFacingDirection().getOpposite())
            return;
        // propagate through
        HardLightBridgeEmitterBlock.updateEmission(level, state, pos, true);
    }

    @Override
    public void beforePortalRemove(ServerLevel level, BlockState state, BlockPos pos, Portal portal) {
        Direction facing = state.getValue(FACING);
        if (facing == portal.getFacingDirection().getOpposite()) { // portal in front, remove bridge on other side.
            HardLightBridgeEmitterBlock.updateEmission(level, state, pos, false);
        }
    }
}
