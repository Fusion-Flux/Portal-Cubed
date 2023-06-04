package com.fusionflux.portalcubed.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockCollisionsExt {
    static Iterable<VoxelShape> wrapBlockCollisions(Iterable<VoxelShape> original, Entity entity) {
        final VoxelShape cutout = CalledValues.getPortalCutout(entity);
        return () -> ((BlockCollisionsExt)original.iterator()).setPortalCutout(cutout);
    }

    BlockCollisions setPortalCutout(VoxelShape cutout);
}
