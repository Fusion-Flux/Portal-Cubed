package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LaserRelayBlock extends AbstractLaserNodeBlock {
    public static final DirectionProperty FACING = Properties.FACING;

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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DIRECTION_TO_SHAPE.get(state.get(FACING));
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
}
