package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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


public class ExcursionFunnelEmitter extends BlockWithEntity {

    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);


    public ExcursionFunnelEmitter(Settings settings) {
        super(settings);
    }


    @Override
    public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return method_31018(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return method_31018(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return method_31018(state);
    }

    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (blockState.get(Properties.FACING)==Direction.UP) {
            voxelShape = UP_SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.DOWN) {
            voxelShape = DOWN_SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.NORTH) {
            voxelShape = NORTH_SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.SOUTH) {
            voxelShape = SOUTH_SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.EAST) {
            voxelShape = EAST_SHAPE;
        }

        if (blockState.get(Properties.FACING)==Direction.WEST) {
            voxelShape = WEST_SHAPE;
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
    public BlockEntity createBlockEntity(BlockView world) {
        return new ExcursionFunnelEmitterEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING, Properties.POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return ThinkingWithPortatosBlocks.EXCURSION_FUNNEL_EMITTER.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite()).with(Properties.POWERED, false);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            ((ExcursionFunnelEmitterEntity) Objects.requireNonNull(world.getBlockEntity(pos))).spookyUpdateObstructor(pos);
        }
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
        if(world.isClient() && world.getBlockState(pos).get(Properties.POWERED)) {
            ExcursionFunnel.addCollisionEffects(world, entity, pos);
        }
    }
}