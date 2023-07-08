package com.fusionflux.portalcubed.blocks.bridge;

import com.fusionflux.portalcubed.blocks.PortalMoveListeningBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface HardLightBridgePart extends PortalMoveListeningBlock {
    DirectionProperty FACING = BlockStateProperties.FACING;
    EnumProperty<Edge> EDGE = EnumProperty.create("edge", Edge.class);
}

