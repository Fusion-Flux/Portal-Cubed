package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
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
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

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

    public static final Map<Direction, BooleanProperty> propertyMap;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 12.0D, 0.0D, 14.0D, 13.0D, 16.0D);
    protected static final VoxelShape SHAPEROTATED = Block.createCuboidShape(0.0D, 12.0D, 2.0D, 16.0D, 13.0D, 14.0D);



    private final Map<BlockState, VoxelShape> field_26659;



    public HardLightBridgeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
        this.field_26659 = ImmutableMap.copyOf((Map) this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeBlock::method_31018)));
       // this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
        //this.field_26659 = ImmutableMap.copyOf((Map) this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeBlock::method_31018)));
    }

    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if(neighborState.getBlock() == PortalCubedBlocks.HLB_BLOCK) {
            //if(pos.mutableCopy().move(rotateClockwise(state.get(Properties.FACING))) == neighborPos || pos.mutableCopy().move(rotateClockwise(state.get(Properties.FACING).getOpposite())) == neighborPos )
           /* if (neighborState.get(Properties.FACING) != state.get(Properties.FACING)
                    && neighborState.get(Properties.FACING).getOpposite() != state.get(Properties.FACING)
                    && state.get(Properties.FACING) != direction) {

                    BlockPos testa = pos;
                BlockPos testb = pos;

                testa.mutableCopy().move(rotateClockwise(state.get(Properties.FACING)));
                testb.mutableCopy().move(rotateClockwise(state.get(Properties.FACING)).getOpposite());

                if(world.getBlockState(testa).getBlock() == PortalCubedBlocks.HLB_BLOCK || world.getBlockState(testb).getBlock() == PortalCubedBlocks.HLB_BLOCK) {

                    BooleanProperty booleanProperty = getFacingProperty(neighborState.get(Properties.FACING));
                    return state.with(booleanProperty, true);
                }
            }*/
        }
        return state;
    }

    public static BooleanProperty getFacingProperty(Direction direction) {
        return propertyMap.get(direction);
    }

    public Direction rotateClockwise (Direction start){
        if(start == Direction.NORTH){
            return Direction.EAST;
        }
        if(start == Direction.EAST){
            return Direction.SOUTH;
        }
        if(start == Direction.SOUTH){
            return Direction.WEST;
        }
        if(start == Direction.WEST){
            return Direction.NORTH;
        }
        return start;
    }


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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING,NORTH, EAST, WEST, SOUTH, UP, DOWN);
    }




    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();


        if (blockState.get(Properties.FACING)==Direction.NORTH) {
            voxelShape =SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.SOUTH) {
            voxelShape = SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.EAST) {
            voxelShape = SHAPEROTATED;
        }

        if (blockState.get(Properties.FACING)==Direction.WEST) {
            voxelShape = SHAPEROTATED;
        }

        if (blockState.get(WEST)||blockState.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEROTATED);
        }

        if (blockState.get(NORTH)||blockState.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE);
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

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

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