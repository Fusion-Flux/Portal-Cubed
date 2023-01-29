package com.fusionflux.portalcubed.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class OldApBlock extends Block {
    public OldApBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.getBlock() instanceof OldApBlock;
    }

}
