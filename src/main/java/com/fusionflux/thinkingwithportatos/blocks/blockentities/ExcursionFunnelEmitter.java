package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

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

    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (blockState.get(Properties.FACING) == Direction.UP) {
            voxelShape = UP_SHAPE;
        }

        if (blockState.get(Properties.FACING) == Direction.DOWN) {
            voxelShape = DOWN_SHAPE;
        }

        if (blockState.get(Properties.FACING) == Direction.NORTH) {
            voxelShape = NORTH_SHAPE;
        }

        if (blockState.get(Properties.FACING) == Direction.SOUTH) {
            voxelShape = SOUTH_SHAPE;
        }

        if (blockState.get(Properties.FACING) == Direction.EAST) {
            voxelShape = EAST_SHAPE;
        }

        if (blockState.get(Properties.FACING) == Direction.WEST) {
            voxelShape = WEST_SHAPE;
        }

        return voxelShape;
    }



    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return method_31018(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return method_31018(state);
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
        this.addCollisionEffects(world, entity, pos);
    }


    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        if (world.isClient() && world.getBlockState(pos).get(Properties.POWERED)) {
            BlockState state = world.getBlockState(pos);
            double xoffset = (entity.getPos().getX() - pos.getX()) - .5;
            double yoffset = (entity.getPos().getY() - pos.getY()) - 1.25;
            double zoffset = (entity.getPos().getZ() - pos.getZ()) - .5;
            Vec3d direction = new Vec3d(0, 0, 0);
            direction = new Vec3d(state.get(Properties.FACING).getVector().getX(), state.get(Properties.FACING).getVector().getY(), state.get(Properties.FACING).getVector().getZ());
            direction = direction.multiply(.1);
            if (direction.x != 0) {
                entity.setVelocity(direction.getX(), 0 - yoffset * .048, entity.getVelocity().getZ() - (zoffset / Math.abs(zoffset)) * .01);
            }
            if (direction.y != 0) {
                entity.setVelocity(entity.getVelocity().getX() - (xoffset / Math.abs(xoffset)) * .01, 0.08 + direction.getY(), entity.getVelocity().getZ() - (zoffset / Math.abs(zoffset)) * .01);
            }
            if (direction.z != 0) {
                entity.setVelocity(entity.getVelocity().getX() - (xoffset / Math.abs(xoffset)) * .01, 0 - yoffset * .048, direction.getZ());
            }
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcursionFunnelEmitterEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ThinkingWithPortatosBlocks.EXCURSION_FUNNEL_EMMITER_ENTITY, ExcursionFunnelEmitterEntity::tick);
    }

}