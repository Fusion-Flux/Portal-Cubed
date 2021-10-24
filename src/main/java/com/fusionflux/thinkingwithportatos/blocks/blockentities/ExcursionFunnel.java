package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.EntityAttachments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class ExcursionFunnel extends BlockWithEntity {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public ExcursionFunnel(Settings settings) {
        super(settings);
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
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
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(ThinkingWithPortatosBlocks.EXCURSION_FUNNEL_EMITTER)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(ThinkingWithPortatosBlocks.HLB_BLOCK);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos);
    }


    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        if (world.isClient()) {
            BlockState state = world.getBlockState(pos);
            double xoffset = (entity.getPos().getX() - pos.getX()) - .5;
            double yoffset = (entity.getPos().getY() - pos.getY()) + .5;
            double zoffset = (entity.getPos().getZ() - pos.getZ()) - .5;
            Vec3d direction = new Vec3d(0, 0, 0);
            direction = new Vec3d(state.get(Properties.FACING).getVector().getX(), state.get(Properties.FACING).getVector().getY(), state.get(Properties.FACING).getVector().getZ());
            direction = direction.multiply(.1);
            //entity.setSwimming(true);
            entity.setNoGravity(true);

            if(!((EntityAttachments)entity).isInFunnel()){
                ((EntityAttachments)entity).setInFunnel(true);
                entity.setVelocity(0,0,0);
            }

                ((EntityAttachments) entity).setFunnelTimer(2);

            /*if(Math.abs(entity.getVelocity().x)>.3){
                entity.setVelocity(0,entity.getVelocity().y,entity.getVelocity().z);
            }
            if(Math.abs(entity.getVelocity().y)>.3){
                entity.setVelocity(entity.getVelocity().x,0,entity.getVelocity().z);
            }
            if(Math.abs(entity.getVelocity().z)>.3){
                entity.setVelocity(entity.getVelocity().x,entity.getVelocity().y,0);
            }*/
            if (direction.x != 0) {
                entity.addVelocity(0, (- (yoffset/Math.abs(yoffset)) * .008),  - (zoffset/Math.abs(zoffset)) * .008);
                entity.setVelocity(direction.getX(),entity.getVelocity().y,entity.getVelocity().z);
            }
            if (direction.y != 0) {
                entity.addVelocity(-(xoffset/Math.abs(xoffset)) * .008, 0,  -(zoffset/Math.abs(zoffset)) * .008);
                entity.setVelocity(entity.getVelocity().x,direction.getY(),entity.getVelocity().z);
            }
            if (direction.z != 0) {
                entity.addVelocity( -(xoffset/Math.abs(xoffset))*.008, (- (yoffset/Math.abs(yoffset)) * .008), 0);
                entity.setVelocity(entity.getVelocity().x,entity.getVelocity().y,direction.getZ());
            }
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcursionFunnelEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ThinkingWithPortatosBlocks.EXCURSION_FUNNEL_ENTITY, ExcursionFunnelEntity::tick);
    }

}