package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BetaFaithPlateBlockEntity extends FaithPlateBlockEntity {
    public BetaFaithPlateBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.BETA_FAITH_PLATE_BLOCK_ENTITY, pos, state);
    }
}
