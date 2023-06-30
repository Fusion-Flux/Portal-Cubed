package com.fusionflux.portalcubed.blocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SimpleMultiSidedBlock extends MultifaceBlock {
    public SimpleMultiSidedBlock(Properties settings) {
        super(settings);
    }

    @NotNull
    @Override
    public MultifaceSpreader getSpreader() {
        return new MultifaceSpreader(this);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().is(asItem()) ? super.canBeReplaced(state, useContext) : state.canBeReplaced();
    }
}
