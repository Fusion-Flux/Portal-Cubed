package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.HardLightBridgeBlockEntity;
import com.fusionflux.portalcubed.util.CustomProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO replace with your own reference as appropriate

public class HardLightBridgeBlock extends BaseEntityBlock {



    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final DirectionProperty VERT_FACING_UP;
    public static final DirectionProperty VERT_FACING_DOWN;

    public static final Map<Direction, BooleanProperty> PROPERTY_MAP;

    protected static final VoxelShape SHAPE = Block.box(2.0D, 1.0D, 0.0D, 14.0D, 2.0D, 16.0D);
    protected static final VoxelShape SHAPE_ROTATED = Block.box(0.0D, 1.0D, 2.0D, 16.0D, 2.0D, 14.0D);

    protected static final VoxelShape VERT_NORTH = Block.box(2.0D, 0.0D, 1.0D, 14.0D, 16.0D, 2.0D);
    protected static final VoxelShape VERT_SOUTH = Block.box(2.0D, 0.0D, 14.0D, 14.0D, 16.0D, 15.0D);
    protected static final VoxelShape VERT_EAST = Block.box(14.0D, 0.0D, 2.0D, 15.0D, 16.0D, 14.0D);
    protected static final VoxelShape VERT_WEST = Block.box(1.0D, 0.0D, 2.0D, 2.0D, 16.0D, 14.0D);
    private final Map<BlockState, VoxelShape> stateToShape;



    public HardLightBridgeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(VERT_FACING_UP, Direction.NORTH).setValue(VERT_FACING_DOWN, Direction.NORTH));
        this.stateToShape = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeBlock::getShapeForState)));
    }

    static {
        NORTH = BlockStateProperties.NORTH;
        EAST = BlockStateProperties.EAST;
        SOUTH = BlockStateProperties.SOUTH;
        WEST = BlockStateProperties.WEST;
        UP = BlockStateProperties.UP;
        DOWN = BlockStateProperties.DOWN;
        VERT_FACING_UP = CustomProperties.HFACINGUP;
        VERT_FACING_DOWN = CustomProperties.HFACINGDOWN;
        PROPERTY_MAP = new HashMap<>();
        PROPERTY_MAP.put(Direction.NORTH, BlockStateProperties.NORTH);
        PROPERTY_MAP.put(Direction.SOUTH, BlockStateProperties.SOUTH);
        PROPERTY_MAP.put(Direction.EAST, BlockStateProperties.EAST);
        PROPERTY_MAP.put(Direction.WEST, BlockStateProperties.WEST);
        PROPERTY_MAP.put(Direction.UP, BlockStateProperties.UP);
        PROPERTY_MAP.put(Direction.DOWN, BlockStateProperties.DOWN);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, VERT_FACING_UP, VERT_FACING_DOWN);
    }




    private static VoxelShape getShapeForState(BlockState blockState) {
        VoxelShape voxelShape = Shapes.empty();


        if (blockState.getValue(WEST) || blockState.getValue(EAST)) {
            voxelShape = Shapes.or(voxelShape, SHAPE_ROTATED);
        }

        if (blockState.getValue(NORTH) || blockState.getValue(SOUTH)) {
            voxelShape = Shapes.or(voxelShape, SHAPE);
        }

        if (blockState.getValue(UP)) {
            if (blockState.getValue(VERT_FACING_UP).equals(Direction.NORTH)) {
                voxelShape = Shapes.or(voxelShape, VERT_NORTH);
            }
            if (blockState.getValue(VERT_FACING_UP).equals(Direction.SOUTH)) {
                voxelShape = Shapes.or(voxelShape, VERT_SOUTH);
            }
            if (blockState.getValue(VERT_FACING_UP).equals(Direction.EAST)) {
                voxelShape = Shapes.or(voxelShape, VERT_EAST);
            }
            if (blockState.getValue(VERT_FACING_UP).equals(Direction.WEST)) {
                voxelShape = Shapes.or(voxelShape, VERT_WEST);
            }
        }

        if (blockState.getValue(DOWN)) {
            if (blockState.getValue(VERT_FACING_DOWN).equals(Direction.NORTH)) {
                voxelShape = Shapes.or(voxelShape, VERT_NORTH);
            }
            if (blockState.getValue(VERT_FACING_DOWN).equals(Direction.SOUTH)) {
                voxelShape = Shapes.or(voxelShape, VERT_SOUTH);
            }
            if (blockState.getValue(VERT_FACING_DOWN).equals(Direction.EAST)) {
                voxelShape = Shapes.or(voxelShape, VERT_EAST);
            }
            if (blockState.getValue(VERT_FACING_DOWN).equals(Direction.WEST)) {
                voxelShape = Shapes.or(voxelShape, VERT_WEST);
            }
        }


        return voxelShape;
    }


    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.stateToShape.get(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.stateToShape.get(state);
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.is(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
            return stateFrom.getValue(BlockStateProperties.POWERED);
        } else return stateFrom.is(PortalCubedBlocks.HLB_BLOCK);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HardLightBridgeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PortalCubedBlocks.HLB_BLOCK_ENTITY, HardLightBridgeBlockEntity::tick);
    }



}
