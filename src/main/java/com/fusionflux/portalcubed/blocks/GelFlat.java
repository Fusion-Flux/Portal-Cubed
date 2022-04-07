package com.fusionflux.portalcubed.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GelFlat extends Block {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final Map<Direction, BooleanProperty> propertyMap;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.01D, 16.0D);
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0D, 15.99D, 0.0D, 16.0D, 16D, 16.0D);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0D, 0.00D, 0.0D, 16.0D, 0.01D, 16.0D);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.00D, 0.0D, 0.0D, 0.01D, 16.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.99D, 0.0D, 0.0D, 16D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.00D, 16.0D, 16.0D, 0.01D);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 15.99D, 16.0D, 16.0D, 16D);

    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().collect(Util.toMap());

    static {
        NORTH = Properties.NORTH;
        EAST = Properties.EAST;
        SOUTH = Properties.SOUTH;
        WEST = Properties.WEST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        propertyMap = new HashMap<>();
        propertyMap.put(Direction.NORTH, Properties.NORTH);
        propertyMap.put(Direction.SOUTH, Properties.SOUTH);
        propertyMap.put(Direction.EAST, Properties.EAST);
        propertyMap.put(Direction.WEST, Properties.WEST);
        propertyMap.put(Direction.UP, Properties.UP);
        propertyMap.put(Direction.DOWN, Properties.DOWN);
    }
    //protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16D, 1.0D, 16);

    private final Map<BlockState, VoxelShape> field_26659;

    public GelFlat(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
        this.field_26659 = ImmutableMap.copyOf((Map) this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), GelFlat::method_31018)));
    }

    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (blockState.get(UP)) {
            voxelShape = UP_SHAPE;
        }

        if (blockState.get(DOWN)) {
            voxelShape = VoxelShapes.union(voxelShape, DOWN_SHAPE);
        }

        if (blockState.get(NORTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        }

        if (blockState.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        }

        if (blockState.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        }

        if (blockState.get(WEST)) {
            voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        }

        return voxelShape;
    }

    public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos);
        return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction.getOpposite());
    }

    public static BooleanProperty getFacingProperty(Direction direction) {
        return propertyMap.get(direction);
    }

    private boolean hasAdjacentBlocks(BlockState state) {
        return this.getAdjacentBlockCount(state) > 0;
    }

    private int getAdjacentBlockCount(BlockState state) {
        int i = 0;

        for (BooleanProperty booleanProperty : propertyMap.values()) {
            if (state.get(booleanProperty)) {
                ++i;
            }
        }

        return i;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_26659.get(state);
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN);
    }

    protected boolean canConnect(WorldAccess world, BlockPos neighborPos) {
        return world.getBlockState(neighborPos).getBlock().getDefaultState().isSolidBlock(world, neighborPos);
    }



    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        boolean bl = blockState.isOf(this);
        BlockState blockState2 = bl ? blockState : this.getDefaultState();
        Direction[] var5 = ctx.getPlacementDirections();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];

            BooleanProperty booleanProperty = getFacingProperty(direction);
            boolean bl2 = bl && blockState.get(booleanProperty);
            if (!bl2 && this.shouldHaveSide(ctx.getWorld(), ctx.getBlockPos(), direction)) {
                return blockState2.with(booleanProperty, true);
            }

        }

        return bl ? blockState2 : null;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos));
    }

    private boolean shouldHaveSide(BlockView world, BlockPos pos, Direction side) {
        BlockPos blockPos = pos.offset(side);
        if (shouldConnectTo(world, blockPos, side)) {
            return true;
        } else if (side.getAxis() == Direction.Axis.Y) {
            return false;
        } else {
            BooleanProperty booleanProperty = propertyMap.get(side);
            BlockState blockState = world.getBlockState(pos.up());
            return blockState.isOf(this) && blockState.get(booleanProperty);
        }

    }

    private BlockState getPlacementShape(BlockState state, BlockView world, BlockPos pos) {
        BlockState blockState = null;
        Iterator var6 = Direction.Type.HORIZONTAL.iterator();

        while (true) {
            Direction direction;
            BooleanProperty booleanProperty;
            do {
                if (!var6.hasNext()) {
                    return state;
                }

                direction = (Direction) var6.next();
                booleanProperty = getFacingProperty(direction);
            } while (!(Boolean) state.get(booleanProperty));

            boolean bl = this.shouldHaveSide(world, pos, direction);
            if (!bl) {
                if (blockState == null) {
                    blockState = Blocks.AIR.getDefaultState();
                }

                bl = blockState.isOf(this) && blockState.get(booleanProperty);
            }

            state = state.with(booleanProperty, bl);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.isOf(this)) {
            return this.getAdjacentBlockCount(blockState) < propertyMap.size();
        } else {
            return super.canReplace(state, context);
        }
    }

    public BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BooleanProperty direction = propertyMap.get(facing);
        BlockState blockState = this.getPlacementShape(state, world, pos);
        return !this.hasAdjacentBlocks(blockState) ? Blocks.AIR.getDefaultState() : blockState.with(direction, false);
    }

    @Override
    public boolean hasDynamicBounds() {
        return this.dynamicBounds;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.hasRain(pos.up())) {
            world.setBlockState(pos,Blocks.AIR.getDefaultState());
        }

    }

}
