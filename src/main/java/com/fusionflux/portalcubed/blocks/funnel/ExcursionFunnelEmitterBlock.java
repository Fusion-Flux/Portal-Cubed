package com.fusionflux.portalcubed.blocks.funnel;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelEmitterBlockEntity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelEmitterBlockEntity.ToggleMode;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.TwoByTwo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExcursionFunnelEmitterBlock extends BaseEntityBlock implements TwoByTwoFacingMultiblockBlock {
    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
    public static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.UP, Block.box(0, 0, 0, 16, 4, 16));
        map.put(Direction.DOWN, Block.box(0, 12, 0, 16, 16, 16));
        map.put(Direction.WEST, Block.box(12, 0, 0, 16, 16, 16));
        map.put(Direction.EAST, Block.box(0, 0, 0, 4, 16, 16));
        map.put(Direction.SOUTH, Block.box(0, 0, 0, 16, 16, 4));
        map.put(Direction.NORTH, Block.box(0, 0, 12, 16, 16, 16));
    });

    static boolean suppressUpdates;

    public ExcursionFunnelEmitterBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODE, Mode.FORWARD_OFF)
                .setValue(QUADRANT, 1)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, QUADRANT);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).is(PortalCubedItems.HAMMER))
            return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, state.getValue(QUADRANT), facing);
        // q1 is primary
        BlockPos q1 = multiblock.byQuadrant(1);
        if (!(level.getBlockEntity(q1) instanceof ExcursionFunnelEmitterBlockEntity emitter))
            return InteractionResult.FAIL;
        ToggleMode mode = emitter.getToggleMode();
        boolean powered = state.getValue(MODE) == mode.on;
        ToggleMode newToggleMode = mode.next();
        emitter.setToggleMode(newToggleMode);
        // sync with others
        multiblock.forEach(part -> {
            if (part != q1 && level.getBlockEntity(part) instanceof ExcursionFunnelEmitterBlockEntity be)
                be.setToggleMode(newToggleMode);
        });
        // update emitter
        Mode newMode = powered ? newToggleMode.on : newToggleMode.off;
        updateEmitter(serverLevel, multiblock, newMode);
        updateEmissionSuppressed(serverLevel, multiblock, facing, newMode);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING,
                context.getPlayer() == null ? Direction.SOUTH : context.getNearestLookingDirection()
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return FACING_TO_SHAPE.get(facing);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        // this makes the end texture only render on the very end
        if (!adjacentState.is(this) && !adjacentState.is(PortalCubedBlocks.EXCURSION_FUNNEL))
            return false;
        Direction facing = state.getValue(FACING);
        if (facing != direction)
            return false;
        Direction adjacentFacing = adjacentState.getValue(FACING);
        return adjacentFacing == facing;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        Mode mode = state.getValue(MODE);
        if (!mode.isOn)
            return;
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, state.getValue(QUADRANT), facing);
        Direction motion = mode.isReversed ? facing.getOpposite() : facing;
        ExcursionFunnelTubeBlock.applyEffects(entity, multiblock.getCenter(), motion);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(level instanceof ServerLevel serverLevel) || suppressUpdates)
            return;
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(
                pos, state.getValue(QUADRANT), facing
        );
        if (block == PortalCubedBlocks.EXCURSION_FUNNEL || multiblock.contains(fromPos))
            return; // ignore updates from self and funnels
        if (!(level.getBlockEntity(pos) instanceof ExcursionFunnelEmitterBlockEntity be))
                return;
        boolean updateAnyway = false;
        if (fromPos.equals(pos.relative(facing))) { // pos in front
            BlockState newState = level.getBlockState(fromPos);
            if (newState.isAir())
                updateAnyway = true; // block in front removed, re-emit
        }
        boolean anyPowered = anyPartPowered(level, multiblock);
        Mode currentMode = state.getValue(MODE);
        ToggleMode toggleMode = be.getToggleMode();
        boolean currentlyPowered = toggleMode.on == currentMode;
        if (!updateAnyway && currentlyPowered == anyPowered)
            return; // up to date
        Mode newMode = anyPowered ? toggleMode.on : toggleMode.off;
        updateEmitter(serverLevel, multiblock, newMode);
        updateEmissionSuppressed(serverLevel, multiblock, facing, newMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!suppressUpdates && level instanceof ServerLevel serverLevel && !state.is(newState.getBlock()))
            destroyMultiblock(serverLevel, state, pos, true);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!suppressUpdates && level instanceof ServerLevel serverLevel && player.isCreative())
            destroyMultiblock(serverLevel, state, pos, false); // intercept the destruction in creative to prevent drops
        super.playerWillDestroy(level, pos, state, player);
    }

    public void updateEmitter(ServerLevel level, TwoByTwo multiblock, Mode newMode) {
        multiblock.forEach(part -> {
            BlockState partState = level.getBlockState(part);
            if (!partState.is(this))
                return;
            BlockState newState = partState.setValue(MODE, newMode);
            level.setBlockAndUpdate(part, newState);
        });
    }

    public static void updateEmissionSuppressed(ServerLevel level, TwoByTwo multiblock, Direction facing, Mode newMode) {
        withUpdatesSuppressed(() -> updateEmission(level, multiblock, facing, newMode));
    }

    private static void updateEmission(ServerLevel level, TwoByTwo multiblock, Direction facing, Mode newMode) {
        TwoByTwo tubeBase = multiblock;
        BlockState tubeStateBase = !newMode.isOn ? Blocks.AIR.defaultBlockState()
                : PortalCubedBlocks.EXCURSION_FUNNEL.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(ExcursionFunnelTubeBlock.REVERSED, newMode.isReversed);
        BlockPos[] positionsToPlace = new BlockPos[4];
        BlockState[] statesToPlace = new BlockState[4];

        distance: for (int i = 1; i < PortalCubedConfig.maxBridgeLength; i++) {
            for (int q = 0; q < 4; q++) {
                BlockPos quadrant = tubeBase.byQuadrantIndex(q);
                BlockPos offset = quadrant.relative(facing, i);

                if (!canPlaceFunnel(level, offset, facing)) { // hit a wall. check for portals. TODO!
//                    AABB bounds = tubeBase.toBox(3).move(offset.subtract(quadrant));
//                    List<Portal> portals = level.getEntities(PortalCubedEntities.PORTAL, bounds, p -> mayPassThroughPortal(p, bounds));
//                    if (portals.isEmpty())
                        return; // none found, all done.
//                    Portal portal = portals.get(0); // only care about 1
//
//                    Optional<UUID> linkedId = portal.getLinkedPortalUUID();
//                    if (linkedId.isEmpty())
//                        return;
//                    if (!(level.getEntity(linkedId.get()) instanceof Portal linked))
//                        return;
//
//                    facing = linked.getFacingDirection();
//                    BlockPos otherSide = BlockPos.containing(linked.getOriginPos());
//                    tubeBase = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(otherSide, q + 1, facing);
//                    i = 0;
//                    continue distance;
                }

                // store the state and pos for if all 4 quadrants succeed
                BlockState partState = level.getBlockState(multiblock.byQuadrantIndex(q));
                statesToPlace[q] = getTubeState(partState, tubeStateBase);
                positionsToPlace[q] = offset;

                if (q == 3) { // 4th quadrant, all good. go back through all quadrants and place.
                    for (int j = 0; j < 4; j++) {
                        level.setBlockAndUpdate(positionsToPlace[j], statesToPlace[j]);
                    }
                }
            }
        }
    }

    private static BlockState getTubeState(BlockState emitter, BlockState base) {
        if (base.isAir() || !emitter.hasProperty(QUADRANT))
            return base;
        Integer quadrant = emitter.getValue(QUADRANT);
        return base.setValue(QUADRANT, quadrant);
    }

    protected void destroyMultiblock(ServerLevel level, BlockState state, BlockPos thisPos, boolean dropItem) {
        if (suppressUpdates)
            return;
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(
                thisPos, state.getValue(QUADRANT), state.getValue(FACING)
        );
        for (BlockPos pos : multiblock) {
            if (pos != thisPos && level.getBlockState(pos).is(this)) {
                level.destroyBlock(pos, dropItem);
            }
        }
        Mode mode = state.getValue(MODE);
        if (mode.isOn) // remove the funnel
            updateEmissionSuppressed(level, multiblock, state.getValue(FACING), Mode.FORWARD_OFF);
    }

    private static boolean anyPartPowered(Level level, TwoByTwo multiblock) {
        boolean anyPowered = false;
        for (BlockPos partPos : multiblock) {
            if (level.hasNeighborSignal(partPos)) {
                anyPowered = true;
                break;
            }
        }
        return anyPowered;
    }

    public static boolean canPlaceFunnel(Level level, BlockPos pos, Direction facing) {
        if (!level.isLoaded(pos))
            return false;
        BlockState state = level.getBlockState(pos);
        // prevent crossing funnels from fighting
        if (state.is(PortalCubedBlocks.EXCURSION_FUNNEL)) {
            return state.getValue(FACING) == facing;
        }
        return state.canBeReplaced();
    }

    public static void withUpdatesSuppressed(Runnable runnable) {
        try {
            suppressUpdates = true;
            runnable.run();
        } finally {
            suppressUpdates = false;
        }
    }

    private static boolean mayPassThroughPortal(Portal portal, AABB bounds) {
        if (portal.isRemoved())
            return false;
        AABB portalBounds = portal.getBoundingBox();
        // must be fully encapsulated
        if (bounds.contains(portalBounds.minX, portalBounds.minY, portalBounds.minZ))
            return bounds.contains(portalBounds.maxX, portalBounds.maxY, portalBounds.maxZ);
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExcursionFunnelEmitterBlockEntity(pos, state);
    }

    public enum Mode implements StringRepresentable {
        FORWARD_OFF, FORWARD_ON, REVERSED_OFF, REVERSED_ON;

        public final String serialized = name().toLowerCase(Locale.ROOT);
        public final boolean isOn = serialized.contains("on");
        public final boolean isReversed = serialized.contains("reverse");

        @Override
        @NotNull
        public String getSerializedName() {
            return serialized;
        }
    }
}
