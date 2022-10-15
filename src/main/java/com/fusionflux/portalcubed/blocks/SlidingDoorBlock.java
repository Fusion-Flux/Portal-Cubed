package com.fusionflux.portalcubed.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class SlidingDoorBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public static final EnumProperty<DoorHinge> HINGE;
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
        FACING = HorizontalFacingBlock.FACING;
        OPEN = Properties.OPEN;
        HINGE = Properties.DOOR_HINGE;
        POWERED = Properties.POWERED;
        HALF = Properties.DOUBLE_BLOCK_HALF;
        NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 5.0D);
        NORTH_EAST_SHAPE = Block.createCuboidShape(13.0D, 0.0D, 1.0D, 16.0D, 16.0D, 5.0D);
        NORTH_WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 1.0D, 3.0D, 16.0D, 5.0D);
        SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 11.0D, 16.0D, 16.0D, 15.0D);
        SOUTH_EAST_SHAPE = Block.createCuboidShape(13.0D, 0.0D, 11.0D, 16.0D, 16.0D, 15.0D);
        SOUTH_WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 11.0D, 3.0D, 16.0D, 15.0D);
        EAST_SHAPE = Block.createCuboidShape(11.0D, 0.0D, 0.0D, 15.0D, 16.0D, 16.0D);
        EAST_SOUTH_SHAPE = Block.createCuboidShape(11.0D, 0.0D, 13.0D, 15.0D, 16.0D, 16.0D);
        EAST_NORTH_SHAPE = Block.createCuboidShape(11.0D, 0.0D, 0.0D, 15.0D, 16.0D, 3.0D);
        WEST_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 0.0D, 5.0D, 16.0D, 16.0D);
        WEST_NORTH_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 0.0D, 5.0D, 16.0D, 3.0D);
        WEST_SOUTH_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 13.0D, 5.0D, 16.0D, 16.0D);
    }

    protected SlidingDoorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHinge.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        boolean bl = !(Boolean) state.get(OPEN);
        boolean bl2 = state.get(HINGE) == DoorHinge.RIGHT;
        switch (direction) {
            case EAST:
            default:
                return bl ? WEST_SHAPE : (bl2 ? WEST_SOUTH_SHAPE : WEST_NORTH_SHAPE);
            case SOUTH:
                return bl ? NORTH_SHAPE : (bl2 ? NORTH_WEST_SHAPE : NORTH_EAST_SHAPE);
            case WEST:
                return bl ? EAST_SHAPE : (bl2 ? EAST_NORTH_SHAPE : EAST_SOUTH_SHAPE);
            case NORTH:
                return bl ? SOUTH_SHAPE : (bl2 ? SOUTH_EAST_SHAPE : SOUTH_WEST_SHAPE);
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return newState.isOf(this) && newState.get(HALF) != doubleBlockHalf ? state.with(FACING, newState.get(FACING)).with(OPEN, newState.get(OPEN)).with(HINGE, newState.get(HINGE)).with(POWERED, newState.get(POWERED)) : Blocks.AIR.getDefaultState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND:
                return state.get(OPEN);
            case WATER:
                return false;
            case AIR:
                return state.get(OPEN);
            default:
                return false;
        }
    }

    private int getOpenSoundEventId() {
        return this.material == Material.METAL ? 1011 : 1012;
    }

    private int getCloseSoundEventId() {
        return this.material == Material.METAL ? 1005 : 1006;
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        if (blockPos.getY() < 255 && ctx.getWorld().getBlockState(blockPos.up()).canReplace(ctx)) {
            World world = ctx.getWorld();
            boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
            return this.getDefaultState().with(FACING, ctx.getPlayerFacing()).with(HINGE, this.getHinge(ctx)).with(POWERED, bl).with(OPEN, bl).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHinge getHinge(ItemPlacementContext ctx) {
        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getPlayerFacing();
        BlockPos blockPos2 = blockPos.up();
        Direction direction2 = direction.rotateYCounterclockwise();
        BlockPos blockPos3 = blockPos.offset(direction2);
        BlockState blockState = blockView.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.offset(direction2);
        BlockState blockState2 = blockView.getBlockState(blockPos4);
        Direction direction3 = direction.rotateYClockwise();
        BlockPos blockPos5 = blockPos.offset(direction3);
        BlockState blockState3 = blockView.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.offset(direction3);
        BlockState blockState4 = blockView.getBlockState(blockPos6);
        int i = (blockState.isFullCube(blockView, blockPos3) ? -1 : 0) + (blockState2.isFullCube(blockView, blockPos4) ? -1 : 0) + (blockState3.isFullCube(blockView, blockPos5) ? 1 : 0) + (blockState4.isFullCube(blockView, blockPos6) ? 1 : 0);
        boolean bl = blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER;
        boolean bl2 = blockState3.isOf(this) && blockState3.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!bl || bl2) && i <= 0) {
            if ((!bl2 || bl) && i >= 0) {
                int j = direction.getOffsetX();
                int k = direction.getOffsetZ();
                Vec3d vec3d = ctx.getHitPos();
                double d = vec3d.x - (double) blockPos.getX();
                double e = vec3d.z - (double) blockPos.getZ();
                return (j >= 0 || !(e < 0.5D)) && (j <= 0 || !(e > 0.5D)) && (k >= 0 || !(d > 0.5D)) && (k <= 0 || !(d < 0.5D)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
            } else {
                return DoorHinge.LEFT;
            }
        } else {
            return DoorHinge.RIGHT;
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        //if (this.material == Material.METAL) {
            return ActionResult.PASS;
        //} else {
        //    state = state.cycle(OPEN);
        //    world.setBlockState(pos, state, 10);
        //    world.syncWorldEvent(player, state.get(OPEN) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), pos, 0);
        //    return ActionResult.success(world.isClient);
        //}
    }

    public boolean method_30841(BlockState blockState) {
        return blockState.get(OPEN);
    }

    public void setOpen(World world, BlockState blockState, BlockPos blockPos, boolean bl) {
        if (blockState.isOf(this) && blockState.get(OPEN) != bl) {
            world.setBlockState(blockPos, blockState.with(OPEN, bl), 10);
            this.playOpenCloseSound(world, blockPos, bl);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (block != this && bl != state.get(POWERED)) {
            if (bl != state.get(OPEN)) {
                this.playOpenCloseSound(world, pos, bl);
            }

            world.setBlockState(pos, state.with(POWERED, bl).with(OPEN, bl), 2);
        }

    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return state.get(HALF) == DoubleBlockHalf.LOWER ? blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) : blockState.isOf(this);
    }

    private void playOpenCloseSound(World world, BlockPos pos, boolean open) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), open ? SoundEvents.BLOCK_PISTON_EXTEND : SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.MASTER, .3F, 2F);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
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
                world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
            }
        }

    }
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING))).cycle(HINGE);
    }

    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }
}
