package com.fusionflux.thinkingwithportatos.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GelFlat extends Block {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final Map<Direction, BooleanProperty> propertyMap;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    //protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16D, 1.0D, 16);

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

    public GelFlat(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, true));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }


    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.PUSH_ONLY;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH,UP,DOWN);
    }

    protected boolean canConnect(WorldAccess world, BlockPos neighborPos) {
        return world.getBlockState(neighborPos).getBlock().getDefaultState().isSolidBlock(world,neighborPos);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return getDefaultState()
                .with(NORTH, canConnect(world, pos.north()))
                .with(SOUTH, canConnect(world, pos.south()))
                .with(EAST, canConnect(world, pos.east()))
                .with(WEST, canConnect(world, pos.west()))
                .with(UP, canConnect(world, pos.up()))
                .with(DOWN, canConnect(world, pos.down()));
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos;
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return true;
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
        //if (!canConnect(world, neighborPos)) return Blocks.AIR.getDefaultState();
        return direction == null ? state : state.with(direction, canConnect(world, neighborPos));
    }

    @Override
    public boolean hasDynamicBounds() {
        return this.dynamicBounds;
    }

}
