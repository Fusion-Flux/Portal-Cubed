package com.fusionflux.portalcubed.blocks.bridge;

import com.fusionflux.portalcubed.blocks.PortalMoveListeningBlock;
import com.fusionflux.portalcubed.entity.Portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface HardLightBridgePart extends PortalMoveListeningBlock {
    DirectionProperty FACING = BlockStateProperties.FACING;
    EnumProperty<Edge> EDGE = EnumProperty.create("edge", Edge.class);
}

