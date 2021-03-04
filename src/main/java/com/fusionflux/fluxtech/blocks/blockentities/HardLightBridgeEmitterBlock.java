package com.fusionflux.fluxtech.blocks.blockentities;

import com.fusionflux.fluxtech.blocks.FluxTechBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;


public class HardLightBridgeEmitterBlock extends BlockWithEntity {


    public HardLightBridgeEmitterBlock( Settings settings ) {
        super( settings );
    }


    @Override
    public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
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
    public BlockEntity createBlockEntity(BlockView world) {
        return new HardLightBridgeEmitterBlockEntity();
    }
    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder) {
        builder.add( Properties.FACING,Properties.POWERED );
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)FluxTechBlocks.HLB_EMITTER_BLOCK.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing().getOpposite()).with( Properties.POWERED, false );
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if( !world.isClient ) {
            ((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull( world.getBlockEntity( pos ) ) ).spookyUpdateObstructor( pos );
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(Properties.FACING, rotation.rotate((Direction)state.get(Properties.FACING)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible( BlockState state, BlockState stateFrom, Direction direction ) {
        if( stateFrom.isOf( FluxTechBlocks.HLB_EMITTER_BLOCK )) {
            return stateFrom.get( Properties.POWERED );
        }
        else return stateFrom.isOf( FluxTechBlocks.HLB_BLOCK );
    }

}