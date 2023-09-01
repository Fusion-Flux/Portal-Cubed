package com.fusionflux.portalcubed.accessor;

import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockCollisionsExt<T> {
	BlockCollisions<T> setExtraShapes(VoxelShape cutout, VoxelShape crossCollision);
}
