package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RocketTurretBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final VoxelShape BASE_SHAPE = VoxelShapes.union(
        createCuboidShape(0, 0, 0, 16, 17, 16),
        createCuboidShape(1.97, 16.97, 1.97, 14.03, 18.03, 14.03)
    );
    public static final VoxelShape POWERED_SHAPE = VoxelShapes.union(
        BASE_SHAPE,
        createCuboidShape(4, 16, 4, 12, 33, 12)
    );
    public static final VoxelShape POWERED_COLLISION_SHAPE = VoxelShapes.union(
        BASE_SHAPE,
        createCuboidShape(4, 16, 4, 12, 32, 12)
    );

    public RocketTurretBlock(Settings settings) {
        super(settings);
        setDefaultState(
            getStateManager().getDefaultState()
                .with(POWERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(POWERED) ? POWERED_SHAPE : BASE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(POWERED) ? POWERED_COLLISION_SHAPE : BASE_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RocketTurretBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean powered = world.isReceivingRedstonePower(pos);
        if (!getDefaultState().isOf(block) && powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY
            ? (world1, pos, state1, entity) -> ((RocketTurretBlockEntity)entity).tick(world1, pos, state1)
            : null;
    }
}
