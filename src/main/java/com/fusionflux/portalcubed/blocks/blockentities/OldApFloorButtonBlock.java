package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class OldApFloorButtonBlock extends BlockWithEntity {
    public static final BooleanProperty ENABLE;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected static final VoxelShape SHAPEUP = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 18.0D, 15.0D);

    protected static final VoxelShape SHAPEUP2 = Block.createCuboidShape(-1.0D, 16.0D, -1.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPEDOWN = Block.createCuboidShape(1.0D, -2.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPEDOWN2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 17.0D, 0.0D, 17.0D);


    protected static final VoxelShape SHAPENORTH = Block.createCuboidShape(1.0D, 1.0D, -2.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPENORTH2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 17.0D, 17.0D, 0.0D);


    protected static final VoxelShape SHAPESOUTH = Block.createCuboidShape(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 18.0D);

    protected static final VoxelShape SHAPESOUTH2 = Block.createCuboidShape(-1.0D, -1.0D, 16.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPEWEST = Block.createCuboidShape(-2.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPEWEST2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 0.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPEEAST = Block.createCuboidShape(0.0D, 1.0D, 1.0D, 18.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPEEAST2 = Block.createCuboidShape(16.0D, -1.0D, -1.0D, 17.0D, 17.0D, 17.0D);


    public OldApFloorButtonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ENABLE,false));
    }

    static {
        ENABLE = Properties.ENABLED;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(Properties.ENABLED);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if(state.get(Properties.ENABLED)){
            return 15;
        }
        return 0;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(Properties.FACING);

        VoxelShape voxelShape = VoxelShapes.empty();

        if(facing == Direction.UP)
            voxelShape = VoxelShapes.union(voxelShape,SHAPEUP);
        if(facing == Direction.DOWN)
            voxelShape = VoxelShapes.union(voxelShape,SHAPEDOWN);
        if(facing == Direction.NORTH)
            voxelShape = VoxelShapes.union(voxelShape,SHAPENORTH);
        if(facing == Direction.SOUTH)
            voxelShape = VoxelShapes.union(voxelShape,SHAPESOUTH);
        if(facing == Direction.EAST)
            voxelShape = VoxelShapes.union(voxelShape,SHAPEEAST);
        if(facing == Direction.WEST)
            voxelShape = VoxelShapes.union(voxelShape,SHAPEWEST);

        voxelShape = VoxelShapes.union(voxelShape,SHAPE);
        return voxelShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(Properties.FACING);

        VoxelShape voxelShape = VoxelShapes.empty();

        if(facing == Direction.UP) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEUP);
            voxelShape = VoxelShapes.union(voxelShape,SHAPEUP2);
        }
        if(facing == Direction.DOWN) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEDOWN);
            voxelShape = VoxelShapes.union(voxelShape, SHAPEDOWN2);
        }
        if(facing == Direction.NORTH) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPENORTH);
            voxelShape = VoxelShapes.union(voxelShape, SHAPENORTH2);
        }
        if(facing == Direction.SOUTH) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPESOUTH);
            voxelShape = VoxelShapes.union(voxelShape, SHAPESOUTH2);
        }
        if(facing == Direction.EAST) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEEAST);
            voxelShape = VoxelShapes.union(voxelShape, SHAPEEAST2);
        }
        if(facing == Direction.WEST) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPEWEST);
            voxelShape = VoxelShapes.union(voxelShape, SHAPEWEST2);
        }

        voxelShape = VoxelShapes.union(voxelShape,SHAPE);

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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING,Properties.ENABLED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return PortalCubedBlocks.OLD_AP_FLOOR_BUTTON.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OldApFloorButtonBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY, OldApFloorButtonBlockEntity::tick1);
    }

}