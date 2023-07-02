package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.Portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface PortalMoveListeningBlock {
    void onPortalCreate(ServerLevel level, BlockState state, BlockPos pos, Portal portal);

    void beforePortalRemove(ServerLevel level, BlockState state, BlockPos pos, Portal portal);
}
