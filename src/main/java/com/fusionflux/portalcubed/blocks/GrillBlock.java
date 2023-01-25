package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;


public class GrillBlock extends Block {
    public static final BooleanProperty NS = BooleanProperty.of("ns");
    public static final BooleanProperty EW = BooleanProperty.of("ew");

    public GrillBlock(Settings settings) {
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NS, EW);
    }
}
