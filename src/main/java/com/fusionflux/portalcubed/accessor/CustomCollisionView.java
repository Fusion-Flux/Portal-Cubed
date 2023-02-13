package com.fusionflux.portalcubed.accessor;

import com.google.common.collect.Iterables;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomCollisionView extends CollisionView {

    default Iterable<VoxelShape> getPortalBlockCollisions(@Nullable Entity entity, Box box, VoxelShape portalBox) {
        return () -> new CustomBlockCollisionSpliterator(this, entity, box, portalBox);
    }

    default Iterable<VoxelShape> getPortalCollisions(@Nullable Entity entity, Box box, VoxelShape portalBox) {
        List<VoxelShape> list = this.getEntityCollisions(entity, box);
        Iterable<VoxelShape> iterable = this.getPortalBlockCollisions(entity, box, portalBox);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

}
