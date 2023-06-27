package com.fusionflux.portalcubed.blocks.bridge;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface HardLightBridgePart {
    DirectionProperty FACING = BlockStateProperties.FACING;
    EnumProperty<Edge> EDGE = EnumProperty.create("edge", Edge.class);
}

