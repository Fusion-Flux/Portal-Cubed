package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class TallButtonVariant extends WallMountedBlock {
    public static final BooleanProperty POWERED;
    protected static final VoxelShape CEILING_X_SHAPE;
    protected static final VoxelShape CEILING_Z_SHAPE;
    protected static final VoxelShape FLOOR_X_SHAPE;
    protected static final VoxelShape FLOOR_Z_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape CEILING_X_PRESSED_SHAPE;
    protected static final VoxelShape CEILING_Z_PRESSED_SHAPE;
    protected static final VoxelShape FLOOR_X_PRESSED_SHAPE;
    protected static final VoxelShape FLOOR_Z_PRESSED_SHAPE;
    protected static final VoxelShape NORTH_PRESSED_SHAPE;
    protected static final VoxelShape SOUTH_PRESSED_SHAPE;
    protected static final VoxelShape WEST_PRESSED_SHAPE;
    protected static final VoxelShape EAST_PRESSED_SHAPE;
    private final boolean wooden;

    protected TallButtonVariant(boolean wooden, Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(FACE, WallMountLocation.WALL));
        this.wooden = wooden;
    }

    private int getPressTicks() {
        return 30;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        boolean bl = state.get(POWERED);
        switch (state.get(FACE)) {
            case FLOOR -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    return bl ? FLOOR_X_PRESSED_SHAPE : FLOOR_X_SHAPE;
                }
                return bl ? FLOOR_Z_PRESSED_SHAPE : FLOOR_Z_SHAPE;
            }
            case WALL -> {
                return switch (direction) {
                    case EAST -> bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
                    case WEST -> bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
                    case SOUTH -> bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
                    default -> bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
                };
            }
            default -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    return bl ? CEILING_X_PRESSED_SHAPE : CEILING_X_SHAPE;
                } else {
                    return bl ? CEILING_Z_PRESSED_SHAPE : CEILING_Z_SHAPE;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(POWERED)) {
            return ActionResult.PASS;
        } else {
            this.powerOn(state, world, pos);
            this.playClickSound(player, world, pos, true);
            return ActionResult.success(world.isClient);
        }
    }

    public void powerOn(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(POWERED, true), 3);
        this.updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, this, this.getPressTicks());
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS, 0.8f, 1f);
    }

    public abstract SoundEvent getClickSound(boolean powered);

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            if (state.get(POWERED)) {
                this.updateNeighbors(state, world, pos);
            }

            super.onStateReplaced(state, world, pos, newState, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && getDirection(state) == direction ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        if (state.get(POWERED)) {
            if (this.wooden) {
                this.tryPowerWithProjectiles(state, world, pos);
            } else {
                world.setBlockState(pos, state.with(POWERED, false), 3);
                this.updateNeighbors(state, world, pos);
                this.playClickSound(null, world, pos, false);
            }

        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.wooden && !(Boolean)state.get(POWERED)) {
            this.tryPowerWithProjectiles(state, world, pos);
        }
    }

    private void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
        List<? extends Entity> list = world.getNonSpectatingEntities(PersistentProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos));
        boolean bl = !list.isEmpty();
        boolean bl2 = state.get(POWERED);
        if (bl != bl2) {
            world.setBlockState(pos, state.with(POWERED, bl), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, bl);
        }

        if (bl) {
            world.scheduleBlockTick(new BlockPos(pos), this, this.getPressTicks());
        }

    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE);
    }

    static {
        POWERED = Properties.POWERED;
        CEILING_X_SHAPE = Block.createCuboidShape(5.5D, -4.0D, 5.5D, 10.5D, 16.0D, 10.5D);
        CEILING_Z_SHAPE = Block.createCuboidShape(5.5D, -4.0D, 5.5D, 10.5D, 16.0D, 10.5D);
        FLOOR_X_SHAPE = Block.createCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 20.0D, 10.5D);
        FLOOR_Z_SHAPE = Block.createCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 20.0D, 10.5D);
        NORTH_SHAPE = Block.createCuboidShape(5.5D, 5.5D, -4.0D, 10.5D, 10.5D, 16.0D);
        SOUTH_SHAPE = Block.createCuboidShape(5.5D, 5.5D, 0.0D, 10.5D, 10.5D, 20.0D);
        WEST_SHAPE = Block.createCuboidShape(-4.0D, 5.5D, 5.5D, 16.0D, 10.5D, 10.5D);
        EAST_SHAPE = Block.createCuboidShape(0.0D, 5.5D, 5.5D, 20.0D, 10.5D, 10.5D);
        CEILING_X_PRESSED_SHAPE = Block.createCuboidShape(5.5D, -4.0D, 5.5D, 10.5D, 16.0D, 10.5D);
        CEILING_Z_PRESSED_SHAPE = Block.createCuboidShape(5.5D, -4.0D, 5.5D, 10.5D, 16.0D, 10.5D);
        FLOOR_X_PRESSED_SHAPE = Block.createCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 20.0D, 10.5D);
        FLOOR_Z_PRESSED_SHAPE = Block.createCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 20.0D, 10.5D);
        NORTH_PRESSED_SHAPE = Block.createCuboidShape(5.5D, 5.5D, -4.0D, 10.5D, 10.5D, 16.0D);
        SOUTH_PRESSED_SHAPE = Block.createCuboidShape(5.5D, 5.5D, 0.0D, 10.5D, 10.5D, 20.0D);
        WEST_PRESSED_SHAPE = Block.createCuboidShape(-4.0D, 5.5D, 5.5D, 16.0D, 10.5D, 10.5D);
        EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0D, 5.5D, 5.5D, 20.0D, 10.5D, 10.5D);
    }
}
