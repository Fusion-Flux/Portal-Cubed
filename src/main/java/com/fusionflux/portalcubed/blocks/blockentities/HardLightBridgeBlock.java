package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.util.CustomProperties;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO replace with your own reference as appropriate

public class HardLightBridgeBlock extends BlockWithEntity {



    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final DirectionProperty VERTFACINGUP;
    public static final DirectionProperty VERTFACINGDOWN;

    public static final Map<Direction, BooleanProperty> propertyMap;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 1.0D, 0.0D, 14.0D, 2.0D, 16.0D);
    protected static final VoxelShape SHAPEROTATED = Block.createCuboidShape(0.0D, 1.0D, 2.0D, 16.0D, 2.0D, 14.0D);

    protected static final VoxelShape VERTNORTH = Block.createCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 16.0D, 2.0D);
    protected static final VoxelShape VERTSOUTH = Block.createCuboidShape(2.0D, 0.0D, 14.0D, 14.0D, 16.0D, 15.0D);
    protected static final VoxelShape VERTEAST = Block.createCuboidShape(14.0D, 0.0D, 2.0D, 15.0D, 16.0D, 14.0D);
    protected static final VoxelShape VERTWEST = Block.createCuboidShape(1.0D, 0.0D, 2.0D, 2.0D, 16.0D, 14.0D);
    private final Map<BlockState, VoxelShape> field_26659;



    public HardLightBridgeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false).with(VERTFACINGUP,Direction.NORTH).with(VERTFACINGDOWN,Direction.NORTH));
        this.field_26659 = ImmutableMap.copyOf((Map) this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeBlock::method_31018)));
    }

    static {
        NORTH = Properties.NORTH;
        EAST = Properties.EAST;
        SOUTH = Properties.SOUTH;
        WEST = Properties.WEST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        VERTFACINGUP = CustomProperties.HFACINGUP;
        VERTFACINGDOWN = CustomProperties.HFACINGDOWN;
        propertyMap = new HashMap<>();
        propertyMap.put(Direction.NORTH, Properties.NORTH);
        propertyMap.put(Direction.SOUTH, Properties.SOUTH);
        propertyMap.put(Direction.EAST, Properties.EAST);
        propertyMap.put(Direction.WEST, Properties.WEST);
        propertyMap.put(Direction.UP, Properties.UP);
        propertyMap.put(Direction.DOWN, Properties.DOWN);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, VERTFACINGUP, VERTFACINGDOWN);
    }




    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();


        if (blockState.get(WEST)||blockState.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEROTATED);
        }

        if (blockState.get(NORTH)||blockState.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE);
        }

        if(blockState.get(UP)){
            if(blockState.get(VERTFACINGUP).equals(Direction.NORTH)){
                voxelShape = VoxelShapes.union(voxelShape, VERTNORTH);
            }
            if(blockState.get(VERTFACINGUP).equals(Direction.SOUTH)){
                voxelShape = VoxelShapes.union(voxelShape, VERTSOUTH);
            }
            if(blockState.get(VERTFACINGUP).equals(Direction.EAST)){
                voxelShape = VoxelShapes.union(voxelShape, VERTEAST);
            }
            if(blockState.get(VERTFACINGUP).equals(Direction.WEST)){
                voxelShape = VoxelShapes.union(voxelShape, VERTWEST);
            }
        }

        if(blockState.get(DOWN)){
            if(blockState.get(VERTFACINGDOWN).equals(Direction.NORTH)){
                voxelShape = VoxelShapes.union(voxelShape, VERTNORTH);
            }
            if(blockState.get(VERTFACINGDOWN).equals(Direction.SOUTH)){
                voxelShape = VoxelShapes.union(voxelShape, VERTSOUTH);
            }
            if(blockState.get(VERTFACINGDOWN).equals(Direction.EAST)){
                voxelShape = VoxelShapes.union(voxelShape, VERTEAST);
            }
            if(blockState.get(VERTFACINGDOWN).equals(Direction.WEST)){
                voxelShape = VoxelShapes.union(voxelShape, VERTWEST);
            }
        }


        return voxelShape;
    }


    @Override
    @Environment(EnvType.CLIENT)
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

  /*  @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }
*/
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_26659.get(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_26659.get(state);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(PortalCubedBlocks.HLB_BLOCK);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HardLightBridgeBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.HLB_BLOCK_ENTITY, HardLightBridgeBlockEntity::tick);
    }



}