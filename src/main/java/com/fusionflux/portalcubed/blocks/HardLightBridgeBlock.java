package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.HardLightBridgeBlockEntity;
import com.fusionflux.portalcubed.util.CustomProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO replace with your own reference as appropriate

public class HardLightBridgeBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final DirectionProperty VERT_FACING_UP;
    public static final DirectionProperty VERT_FACING_DOWN;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 1.0D, 0.0D, 14.0D, 2.0D, 16.0D);
    protected static final VoxelShape SHAPE_ROTATED = Block.createCuboidShape(0.0D, 1.0D, 2.0D, 16.0D, 2.0D, 14.0D);

    protected static final VoxelShape VERT_NORTH = Block.createCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 16.0D, 2.0D);
    protected static final VoxelShape VERT_SOUTH = Block.createCuboidShape(2.0D, 0.0D, 14.0D, 14.0D, 16.0D, 15.0D);
    protected static final VoxelShape VERT_EAST = Block.createCuboidShape(14.0D, 0.0D, 2.0D, 15.0D, 16.0D, 14.0D);
    protected static final VoxelShape VERT_WEST = Block.createCuboidShape(1.0D, 0.0D, 2.0D, 2.0D, 16.0D, 14.0D);
    private final Map<BlockState, VoxelShape> stateToShape;



    public HardLightBridgeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(VERT_FACING_UP, Direction.NORTH).with(VERT_FACING_DOWN, Direction.NORTH));
        this.stateToShape = ImmutableMap.copyOf(this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeBlock::getShapeForState)));
    }

    static {
        FACING = Properties.FACING;
        VERT_FACING_UP = CustomProperties.H_FACING_UP;
        VERT_FACING_DOWN = CustomProperties.H_FACING_DOWN;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERT_FACING_UP, VERT_FACING_DOWN);
    }




    private static VoxelShape getShapeForState(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();


        if (blockState.get(FACING) == Direction.WEST || blockState.get(FACING) == Direction.EAST) {
            return SHAPE_ROTATED;
        }

        if (blockState.get(FACING) == Direction.NORTH || blockState.get(FACING) == Direction.SOUTH) {
            return SHAPE;
        }

        if (blockState.get(FACING) == Direction.UP) {
            if (blockState.get(VERT_FACING_UP).equals(Direction.NORTH)) {
                return VERT_NORTH;
            }
            if (blockState.get(VERT_FACING_UP).equals(Direction.SOUTH)) {
                return VERT_SOUTH;
            }
            if (blockState.get(VERT_FACING_UP).equals(Direction.EAST)) {
                return VERT_EAST;
            }
            if (blockState.get(VERT_FACING_UP).equals(Direction.WEST)) {
                return VERT_WEST;
            }
        }

        if (blockState.get(FACING) == Direction.DOWN) {
            if (blockState.get(VERT_FACING_DOWN).equals(Direction.NORTH)) {
                return VERT_NORTH;
            }
            if (blockState.get(VERT_FACING_DOWN).equals(Direction.SOUTH)) {
                return VERT_SOUTH;
            }
            if (blockState.get(VERT_FACING_DOWN).equals(Direction.EAST)) {
                return VERT_EAST;
            }
            if (blockState.get(VERT_FACING_DOWN).equals(Direction.WEST)) {
                return VERT_WEST;
            }
        }


        return voxelShape;
    }


    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
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
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.stateToShape.get(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.stateToShape.get(state);
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(PortalCubedBlocks.HLB_BLOCK);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HardLightBridgeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.HLB_BLOCK_ENTITY, HardLightBridgeBlockEntity::tick);
    }



}
