package com.fusionflux.portalcubed.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class SlidingDoorBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE;
    public static final BooleanProperty POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape WEST_NORTH_SHAPE;
    protected static final VoxelShape WEST_SOUTH_SHAPE;
    protected static final VoxelShape EAST_NORTH_SHAPE;
    protected static final VoxelShape EAST_SOUTH_SHAPE;
    protected static final VoxelShape NORTH_WEST_SHAPE;
    protected static final VoxelShape SOUTH_WEST_SHAPE;
    protected static final VoxelShape NORTH_EAST_SHAPE;
    protected static final VoxelShape SOUTH_EAST_SHAPE;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
        HINGE = BlockStateProperties.DOOR_HINGE;
        POWERED = BlockStateProperties.POWERED;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        NORTH_SHAPE = Block.box(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 5.0D);
        NORTH_EAST_SHAPE = Block.box(13.0D, 0.0D, 1.0D, 16.0D, 16.0D, 5.0D);
        NORTH_WEST_SHAPE = Block.box(0.0D, 0.0D, 1.0D, 3.0D, 16.0D, 5.0D);
        SOUTH_SHAPE = Block.box(0.0D, 0.0D, 11.0D, 16.0D, 16.0D, 15.0D);
        SOUTH_EAST_SHAPE = Block.box(13.0D, 0.0D, 11.0D, 16.0D, 16.0D, 15.0D);
        SOUTH_WEST_SHAPE = Block.box(0.0D, 0.0D, 11.0D, 3.0D, 16.0D, 15.0D);
        EAST_SHAPE = Block.box(11.0D, 0.0D, 0.0D, 15.0D, 16.0D, 16.0D);
        EAST_SOUTH_SHAPE = Block.box(11.0D, 0.0D, 13.0D, 15.0D, 16.0D, 16.0D);
        EAST_NORTH_SHAPE = Block.box(11.0D, 0.0D, 0.0D, 15.0D, 16.0D, 3.0D);
        WEST_SHAPE = Block.box(1.0D, 0.0D, 0.0D, 5.0D, 16.0D, 16.0D);
        WEST_NORTH_SHAPE = Block.box(1.0D, 0.0D, 0.0D, 5.0D, 16.0D, 3.0D);
        WEST_SOUTH_SHAPE = Block.box(1.0D, 0.0D, 13.0D, 5.0D, 16.0D, 16.0D);
    }

    protected SlidingDoorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        boolean bl = !(Boolean) state.getValue(OPEN);
        boolean bl2 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
        return switch (direction) {
            default -> bl ? WEST_SHAPE : (bl2 ? WEST_SOUTH_SHAPE : WEST_NORTH_SHAPE);
            case SOUTH -> bl ? NORTH_SHAPE : (bl2 ? NORTH_WEST_SHAPE : NORTH_EAST_SHAPE);
            case WEST -> bl ? EAST_SHAPE : (bl2 ? EAST_NORTH_SHAPE : EAST_SOUTH_SHAPE);
            case NORTH -> bl ? SOUTH_SHAPE : (bl2 ? SOUTH_EAST_SHAPE : SOUTH_WEST_SHAPE);
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return newState.is(this) && newState.getValue(HALF) != doubleBlockHalf ? state.setValue(FACING, newState.getValue(FACING)).setValue(OPEN, newState.getValue(OPEN)).setValue(HINGE, newState.getValue(HINGE)).setValue(POWERED, newState.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, newState, world, pos, posFrom);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(OPEN);
            case WATER -> false;
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        if (blockPos.getY() < 255 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx)) {
            Level world = ctx.getLevel();
            boolean bl = world.hasNeighborSignal(blockPos) || world.hasNeighborSignal(blockPos.above());
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HINGE, this.getHinge(ctx)).setValue(POWERED, bl).setValue(OPEN, bl).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHingeSide getHinge(BlockPlaceContext ctx) {
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction direction = ctx.getHorizontalDirection();
        BlockPos blockPos2 = blockPos.above();
        Direction direction2 = direction.getCounterClockWise();
        BlockPos blockPos3 = blockPos.relative(direction2);
        BlockState blockState = blockView.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.relative(direction2);
        BlockState blockState2 = blockView.getBlockState(blockPos4);
        Direction direction3 = direction.getClockWise();
        BlockPos blockPos5 = blockPos.relative(direction3);
        BlockState blockState3 = blockView.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.relative(direction3);
        BlockState blockState4 = blockView.getBlockState(blockPos6);
        int i = (blockState.isCollisionShapeFullBlock(blockView, blockPos3) ? -1 : 0) + (blockState2.isCollisionShapeFullBlock(blockView, blockPos4) ? -1 : 0) + (blockState3.isCollisionShapeFullBlock(blockView, blockPos5) ? 1 : 0) + (blockState4.isCollisionShapeFullBlock(blockView, blockPos6) ? 1 : 0);
        boolean bl = blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean bl2 = blockState3.is(this) && blockState3.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ((!bl || bl2) && i <= 0) {
            if ((!bl2 || bl) && i == 0) {
                int j = direction.getStepX();
                int k = direction.getStepZ();
                Vec3 vec3d = ctx.getClickLocation();
                double d = vec3d.x - (double) blockPos.getX();
                double e = vec3d.z - (double) blockPos.getZ();
                return (j >= 0 || !(e < 0.5D)) && (j <= 0 || !(e > 0.5D)) && (k >= 0 || !(d > 0.5D)) && (k <= 0 || !(d < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean bl = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (block != this && bl != state.getValue(POWERED)) {
            if (bl != state.getValue(OPEN)) {
                this.playOpenCloseSound(world, pos, bl);
            }

            world.setBlock(pos, state.setValue(POWERED, bl).setValue(OPEN, bl), 2);
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = world.getBlockState(blockPos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockState.isFaceSturdy(world, blockPos, Direction.UP) : blockState.is(this);
    }

    private void playOpenCloseSound(Level world, BlockPos pos, boolean open) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), open ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, SoundSource.MASTER, .3F, 2F);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && player.isCreative()) {
            onBreakInCreative(world, pos, state, player);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    public static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)
                        ? Blocks.WATER.defaultBlockState()
                        : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }
}
