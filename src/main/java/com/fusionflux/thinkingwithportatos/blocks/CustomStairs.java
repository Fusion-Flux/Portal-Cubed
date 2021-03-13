package com.fusionflux.thinkingwithportatos.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;

public class CustomStairs extends StairsBlock {

    protected CustomStairs(Block base) {
        super(base.getDefaultState(), FabricBlockSettings.copy(base));
    }
}
