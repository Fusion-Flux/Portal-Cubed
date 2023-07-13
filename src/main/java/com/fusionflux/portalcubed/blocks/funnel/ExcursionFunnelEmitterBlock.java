package com.fusionflux.portalcubed.blocks.funnel;

import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelEmitterBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelEmitterBlockEntity.ToggleMode;
import com.fusionflux.portalcubed.entity.beams.EmittedEntity;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.TwoByTwo;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
        if (!player.getItemInHand(hand).is(PortalCubedItems.WRENCHES))
            return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, state.getValue(QUADRANT), facing);
        if (!(level.getBlockEntity(pos) instanceof ExcursionFunnelEmitterBlockEntity emitter))
            return InteractionResult.FAIL;
        ToggleMode mode = emitter.getToggleMode();
        boolean powered = state.getValue(MODE) == mode.on;
        ToggleMode newToggleMode = mode.next();
        emitter.setToggleMode(newToggleMode);
        // sync with others
        multiblock.forEach(part -> {
            if (part != pos && level.getBlockEntity(part) instanceof ExcursionFunnelEmitterBlockEntity be)
                be.setToggleMode(newToggleMode);
        });
        // update emitter
        Mode newMode = powered ? newToggleMode.on : newToggleMode.off;
        updateEmitter(serverLevel, multiblock, newMode);
        updateEmission(serverLevel, multiblock, facing, newMode);
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
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(
                pos, state.getValue(QUADRANT), facing
        );
        if (multiblock.contains(fromPos))
            return; // ignore updates from self
        if (!(level.getBlockEntity(pos) instanceof ExcursionFunnelEmitterBlockEntity be))
                return;
        boolean anyPowered = anyPartPowered(level, multiblock);
        Mode currentMode = state.getValue(MODE);
        ToggleMode toggleMode = be.getToggleMode();
        boolean currentlyPowered = toggleMode.on == currentMode;
        if (currentlyPowered == anyPowered)
            return; // up to date
        Mode newMode = anyPowered ? toggleMode.on : toggleMode.off;
        updateEmitter(serverLevel, multiblock, newMode);
        updateEmission(serverLevel, multiblock, facing, newMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel && !state.is(newState.getBlock()))
            destroyMultiblock(serverLevel, state, pos, true);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level instanceof ServerLevel serverLevel && player.isCreative())
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

    private static void updateEmission(ServerLevel level, TwoByTwo multiblock, Direction facing, Mode newMode) {
        // remove old entity
        BlockEntity be = level.getBlockEntity(multiblock.byQuadrant(1));
        if (be instanceof ExcursionFunnelEmitterBlockEntity emitter) {
            UUID id = emitter.getFunnelEntityId();
            if (id != null &&  level.getEntity(id) instanceof ExcursionFunnelEntity entity) {
                entity.discard();
            }
        }
        if (newMode.isOn) // spawn new entity if enabled
            spawnFunnelEntity(level, multiblock, facing, newMode.isReversed);
    }

    private static void spawnFunnelEntity(ServerLevel level, TwoByTwo multiblock, Direction facing, boolean reversed) {
        Vec3 start = multiblock.getCenter().relative(facing, -0.3);
        ExcursionFunnelEntity entity = ExcursionFunnelEntity.spawnAndEmit(level, start, facing, reversed, EmittedEntity.MAX_LENGTH);
        // block entity tracks entity
        BlockEntity be = level.getBlockEntity(multiblock.byQuadrant(1));
        if (be instanceof ExcursionFunnelEmitterBlockEntity emitter)
            emitter.setFunnelEntityId(entity.getUUID());
    }

    protected void destroyMultiblock(ServerLevel level, BlockState state, BlockPos thisPos, boolean dropItem) {
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
            updateEmission(level, multiblock, state.getValue(FACING), Mode.FORWARD_OFF);
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
