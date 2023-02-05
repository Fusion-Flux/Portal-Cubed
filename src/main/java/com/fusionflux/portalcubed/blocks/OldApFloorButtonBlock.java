package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.OldApFloorButtonBlockEntity;
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
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class OldApFloorButtonBlock extends BlockWithEntity {
    public static final BooleanProperty ENABLE;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected static final VoxelShape SHAPE_UP = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 18.0D, 15.0D);

    protected static final VoxelShape SHAPE_UP_2 = Block.createCuboidShape(-1.0D, 16.0D, -1.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_DOWN = Block.createCuboidShape(1.0D, -2.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_DOWN_2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 17.0D, 0.0D, 17.0D);


    protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(1.0D, 1.0D, -2.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_NORTH_2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 17.0D, 17.0D, 0.0D);


    protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 18.0D);

    protected static final VoxelShape SHAPE_SOUTH_2 = Block.createCuboidShape(-1.0D, -1.0D, 16.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(-2.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_WEST_2 = Block.createCuboidShape(-1.0D, -1.0D, -1.0D, 0.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0D, 1.0D, 1.0D, 18.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_EAST_2 = Block.createCuboidShape(16.0D, -1.0D, -1.0D, 17.0D, 17.0D, 17.0D);


    public OldApFloorButtonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ENABLE,false));
    }

    static {
        ENABLE = Properties.ENABLED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(Properties.ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if(state.get(Properties.ENABLED)){
            return 15;
        }
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(Properties.FACING);

        VoxelShape voxelShape = VoxelShapes.empty();

        if(facing == Direction.UP)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_UP);
        if(facing == Direction.DOWN)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_DOWN);
        if(facing == Direction.NORTH)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_NORTH);
        if(facing == Direction.SOUTH)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_SOUTH);
        if(facing == Direction.EAST)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_EAST);
        if(facing == Direction.WEST)
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_WEST);

        voxelShape = VoxelShapes.union(voxelShape,SHAPE);
        return voxelShape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(Properties.FACING);

        VoxelShape voxelShape = VoxelShapes.empty();

        if(facing == Direction.UP) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_UP);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_UP_2);
        }
        if(facing == Direction.DOWN) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_DOWN);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_DOWN_2);
        }
        if(facing == Direction.NORTH) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_NORTH);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_NORTH_2);
        }
        if(facing == Direction.SOUTH) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_SOUTH);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_SOUTH_2);
        }
        if(facing == Direction.EAST) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_EAST);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_EAST_2);
        }
        if(facing == Direction.WEST) {
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_WEST);
            voxelShape = VoxelShapes.union(voxelShape, SHAPE_WEST_2);
        }

        voxelShape = VoxelShapes.union(voxelShape,SHAPE);

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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING,Properties.ENABLED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return PortalCubedBlocks.OLD_AP_FLOOR_BUTTON.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OldApFloorButtonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY, OldApFloorButtonBlockEntity::tick1);
    }

}