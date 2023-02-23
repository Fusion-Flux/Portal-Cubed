package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class LaserRelayBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    private static final VoxelShape UP_SHAPE = VoxelShapes.union(
        createCuboidShape(4, 0, 4, 12, 11, 12)
    );
    private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(
        createCuboidShape(4, 6, 4, 12, 16, 12)
    );
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
        createCuboidShape(4, 4, 6, 12, 12, 16)
    );
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SHAPE = Direction.stream()
        .filter(d -> d.getAxis().isHorizontal())
        .collect(Collectors.toMap(
            Function.identity(),
            d -> GeneralUtil.rotate(NORTH_SHAPE, d),
            (m1, m2) -> {
                throw new AssertionError("Sequential stream");
            },
            () -> new EnumMap<>(Direction.class)
        ));

    static {
        DIRECTION_TO_SHAPE.put(Direction.UP, UP_SHAPE);
        DIRECTION_TO_SHAPE.put(Direction.DOWN, DOWN_SHAPE);
    }

    public LaserRelayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ENABLED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(ENABLED) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DIRECTION_TO_SHAPE.get(state.get(FACING));
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
        return PortalCubedBlocks.LASER_RELAY.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserNodeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, LaserNodeBlockEntity::tick);
    }

}
