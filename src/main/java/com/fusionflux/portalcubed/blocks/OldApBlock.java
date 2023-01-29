package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class OldApBlock extends Block {
    public OldApBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState());
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.getBlock() instanceof OldApBlock;
    }

}
