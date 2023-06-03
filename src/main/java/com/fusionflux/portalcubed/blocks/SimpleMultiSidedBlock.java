package com.fusionflux.portalcubed.blocks;

import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;

public class SimpleMultiSidedBlock extends MultifaceBlock {
    public SimpleMultiSidedBlock(Properties settings) {
        super(settings);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return new MultifaceSpreader(this);
    }
}
