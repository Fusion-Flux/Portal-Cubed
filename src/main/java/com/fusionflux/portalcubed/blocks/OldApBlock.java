package com.fusionflux.portalcubed.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class OldApBlock extends Block {
    public OldApBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.getBlock() instanceof OldApBlock;
    }

}
