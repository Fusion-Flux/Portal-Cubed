package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class GrillBlock extends BlockWithEntity {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final DirectionProperty REFLECT_DIRECT;
    public static final BooleanProperty CUBE_REFLECT;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public GrillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false).with(REFLECT_DIRECT,Direction.NORTH).with(CUBE_REFLECT,false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }


    static {
        NORTH = Properties.NORTH;
        EAST = Properties.EAST;
        SOUTH = Properties.SOUTH;
        WEST = Properties.WEST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        REFLECT_DIRECT = Properties.HORIZONTAL_FACING;
        CUBE_REFLECT = CustomProperties.REFLECT;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN,REFLECT_DIRECT,CUBE_REFLECT);
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
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos ,state);
    }



    private void addCollisionEffects(World world, Entity entity, BlockPos pos,BlockState state) {
        if(!world.isClient) {
            List<UUID> portals = CalledValues.getPortals(entity);
            for (UUID portal : portals) {
                Entity checkPortal = ((ServerWorld) world).getEntity(portal);
                if(checkPortal != null) {
                    checkPortal.kill();
                }
            }

            if(entity instanceof CorePhysicsEntity){
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrillBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.GRILL_ENTITY, GrillBlockEntity::tick);
    }

}