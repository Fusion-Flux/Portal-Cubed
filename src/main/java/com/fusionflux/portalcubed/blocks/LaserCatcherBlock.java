package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class LaserCatcherBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);


    public LaserCatcherBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ENABLED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(Properties.ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(Properties.ENABLED)) {
            return 15;
        }
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
        builder.add(FACING, ENABLED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return PortalCubedBlocks.LASER_CATCHER.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserNodeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, LaserNodeBlockEntity::tick);
    }

}
