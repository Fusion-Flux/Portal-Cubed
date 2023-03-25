package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.LaserEmitterBlockEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LaserEmitterBlock extends BlockWithEntity {
    public static final EnumProperty<EmitterDirection> FACING = EnumProperty.of("facing", EmitterDirection.class);
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final VoxelShape OFF_AXIS_SHAPE;

    static {
        final List<VoxelShape> shapes = new ArrayList<>();
        for (int x = -11; x < 11; x++) {
            final int depth = x < 0 ? x + 12 : 11 - x;
            shapes.add(createCuboidShape(
                x + 8, 0, 8 - depth,
                x + 9, 16, 8 + depth
            ));
        }
        final VoxelShape[] shapesArray = shapes.toArray(new VoxelShape[0]);
        OFF_AXIS_SHAPE = VoxelShapes.union(shapesArray[0], Arrays.copyOfRange(shapesArray, 1, shapesArray.length));
    }

    public LaserEmitterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).isOffAxis() ? OFF_AXIS_SHAPE : VoxelShapes.fullCube();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserEmitterBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
            .with(FACING, EmitterDirection.getClosest(ctx.getPlayer()).getOpposite())
            .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean powered = world.isReceivingRedstonePower(pos);
        if (!getDefaultState().isOf(block) && powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);
            if (powered && !world.isClient) {
                world.playSound(null, pos, PortalCubedSounds.LASER_EMITTER_ACTIVATE_EVENT, SoundCategory.BLOCKS, 0.25f, 1f);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, state.get(FACING).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, state.get(FACING).mirror(mirror));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(PortalCubedBlocks.LASER_EMITTER)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(PortalCubedBlocks.HLB_BLOCK);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY
            ? (world1, pos, state1, entity) -> ((LaserEmitterBlockEntity)entity).tick(world1, pos, state1)
            : null;
    }

    public enum EmitterDirection implements StringIdentifiable {
        NORTH(Direction.NORTH),
        NORTHEAST(Direction.NORTH, Direction.EAST),
        EAST(Direction.EAST),
        SOUTHEAST(Direction.SOUTH, Direction.EAST),
        SOUTH(Direction.SOUTH),
        SOUTHWEST(Direction.SOUTH, Direction.WEST),
        WEST(Direction.WEST),
        NORTHWEST(Direction.NORTH, Direction.WEST),
        UP(Direction.UP),
        DOWN(Direction.DOWN);

        private final Vec3d vector, squareVector;

        EmitterDirection(Direction... directions) {
            Vec3d sumVector = Vec3d.of(directions[0].getVector());
            for (int i = 1; i < directions.length; i++) {
                sumVector = sumVector.add(Vec3d.of(directions[i].getVector()));
            }
            vector = sumVector.normalize();
            squareVector = sumVector;
        }

        public static EmitterDirection getClosest(Entity entity) {
            final Direction upOrDown = Direction.getEntityFacingOrder(entity)[0];
            if (upOrDown.getAxis().isVertical()) {
                return upOrDown == Direction.UP ? UP : DOWN;
            }
            float angle = MathHelper.wrapDegrees(entity.getYaw()) + 180;
            if (angle < 22.5f || angle >= 337.5f) {
                return NORTH;
            }
            angle -= 22.5f;
            return values()[(int)(angle / 45f) + 1];
        }

        public static EmitterDirection fromExactVector(Vec3d vector) {
            for (final EmitterDirection direction : values()) {
                if (direction.vector.equals(vector)) {
                    return direction;
                }
            }
            throw new IllegalArgumentException("Vector not a valid EmitterDirection: " + vector);
        }

        public Vec3d getVector() {
            return vector;
        }

        public Vec3d getSquareVector() {
            return squareVector;
        }

        public EmitterDirection getOpposite() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                default -> rotate(BlockRotation.CLOCKWISE_180);
            };
        }

        public EmitterDirection rotate(BlockRotation rotation) {
            return values()[(ordinal() + 2 * rotation.ordinal()) % 8];
        }

        public EmitterDirection mirror(BlockMirror mirror) {
            if (this == UP || this == DOWN) {
                return this;
            }
            return switch (mirror) {
                case NONE -> this;
                case LEFT_RIGHT -> fromExactVector(vector.multiply(1, 1, -1));
                case FRONT_BACK -> fromExactVector(vector.multiply(-1, 1, 1));
            };
        }

        public boolean isOffAxis() {
            return squareVector.lengthSquared() > 1;
        }

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
