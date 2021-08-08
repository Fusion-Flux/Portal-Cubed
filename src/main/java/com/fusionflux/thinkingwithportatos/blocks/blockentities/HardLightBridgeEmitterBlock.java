package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class HardLightBridgeEmitterBlock extends BlockWithEntity {


    public HardLightBridgeEmitterBlock(Settings settings) {
        super(settings);
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
        builder.add(Properties.FACING, Properties.POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return ThinkingWithPortatosBlocks.HLB_EMITTER_BLOCK.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing().getOpposite()).with(Properties.POWERED, false);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            ((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).spookyUpdateObstructor(pos);
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(ThinkingWithPortatosBlocks.HLB_EMITTER_BLOCK)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(ThinkingWithPortatosBlocks.HLB_BLOCK);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HardLightBridgeEmitterBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ThinkingWithPortatosBlocks.HLB_EMITTER_ENTITY, HardLightBridgeEmitterBlockEntity::tick);
    }

}