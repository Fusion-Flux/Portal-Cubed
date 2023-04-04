package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BetaFaithPlateBlockEntity extends FaithPlateBlockEntity {
    public BetaFaithPlateBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.BETA_FAITH_PLATE_BLOCK_ENTITY, pos, state);
    }
}
