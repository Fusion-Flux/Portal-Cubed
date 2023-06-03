package com.fusionflux.portalcubed.listeners;

import com.fusionflux.portalcubed.accessor.CustomBlockCollisionSpliterator;
import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomCollisionView extends CollisionGetter {

    default Iterable<VoxelShape> getPortalBlockCollisions(@Nullable Entity entity, AABB box, VoxelShape portalBox) {
        return () -> new CustomBlockCollisionSpliterator(this, entity, box, portalBox);
    }

    default Iterable<VoxelShape> getPortalCollisions(@Nullable Entity entity, AABB box, VoxelShape portalBox) {
        List<VoxelShape> list = this.getEntityCollisions(entity, box);
        Iterable<VoxelShape> iterable = this.getPortalBlockCollisions(entity, box, portalBox);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

}
