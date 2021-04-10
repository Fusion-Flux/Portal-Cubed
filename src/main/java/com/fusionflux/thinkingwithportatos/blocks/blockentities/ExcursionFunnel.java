package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;



public class ExcursionFunnel extends BlockWithEntity {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public ExcursionFunnel(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
    public BlockEntity createBlockEntity(BlockView world) {
        return new ExcursionFunnelEntity();
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
        this.addCollisionEffects(world, entity,pos);
    }


    private void addCollisionEffects(World world, Entity entity,BlockPos pos) {
        if(world.isClient()){
            BlockState state = world.getBlockState(pos);
            double xoffset = (entity.getPos().getX()-pos.getX())-.5;
            double yoffset = (entity.getPos().getY()-pos.getY())-1.25;
            double zoffset = (entity.getPos().getZ()-pos.getZ())-.5;
            Vec3d direction = new Vec3d(0,0,0);
            direction=new Vec3d(state.get(Properties.FACING).getVector().getX(),state.get(Properties.FACING).getVector().getY(),state.get(Properties.FACING).getVector().getZ());
            direction = direction.multiply(.1);
            if(direction.x!=0){
                entity.setVelocity(direction.getX(),0-yoffset*.048,entity.getVelocity().getZ()-(zoffset/Math.abs(zoffset))*.01);
            }
            if(direction.y!=0){
                entity.setVelocity(entity.getVelocity().getX()-(xoffset/Math.abs(xoffset))*.01,0.08+direction.getY(),entity.getVelocity().getZ()-(zoffset/Math.abs(zoffset))*.01);
            }
            if(direction.z!=0){
                entity.setVelocity(entity.getVelocity().getX()-(xoffset/Math.abs(xoffset))*.01,0-yoffset*.048,direction.getZ());
            }
        }
    }

}