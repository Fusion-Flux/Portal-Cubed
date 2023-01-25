package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public abstract class AbstractFizzlerBlock extends Block {
    public static final BooleanProperty NS = BooleanProperty.of("ns");
    public static final BooleanProperty EW = BooleanProperty.of("ew");

    public AbstractFizzlerBlock(Settings settings) {
        super(settings);
        setDefaultState(
            getStateManager().getDefaultState()
                .with(NS, false)
                .with(EW, false)
        );
    }

    public static BooleanProperty getStateForAxis(Direction.Axis axis) {
        return axis == Direction.Axis.Z ? NS : EW;
    }

    public static boolean isEmpty(BlockState state) {
        return !state.get(NS) && !state.get(EW);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NS, EW);
    }
}
