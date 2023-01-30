package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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

    public static final VoxelShape BASE_SHAPE = createCuboidShape(0, 0, 0, 16, 18, 16);
    public static final VoxelShape POWERED_SHAPE = VoxelShapes.union(
        BASE_SHAPE,
        createCuboidShape(5, 16, 5, 11, 31, 11)
    );

    public RocketTurretBlock(Settings settings) {
        super(settings);
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RocketTurretBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY
            ? (world1, pos, state1, entity) -> ((RocketTurretBlockEntity)entity).tick(world1, pos, state1)
            : null;
    }
}
