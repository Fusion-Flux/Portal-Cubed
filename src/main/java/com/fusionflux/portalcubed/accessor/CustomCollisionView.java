package com.fusionflux.portalcubed.accessor;

import com.google.common.collect.Iterables;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomCollisionView extends BlockView {

    @Nullable
    BlockView getChunkAsView(int chunkX, int chunkZ);

    WorldBorder getWorldBorder();

    default Iterable<VoxelShape> getPortalBlockCollisions(@Nullable Entity entity, Box box, Direction direction) {
        return () -> new CustomBlockCollisionSpliteraror(this, entity, box,direction);
    }

    default Iterable<VoxelShape> getPortalCollisions(@Nullable Entity entity, Box box,Direction direction) {
        List<VoxelShape> list = this.getEntityCollisions(entity, box);
        Iterable<VoxelShape> iterable = this.getPortalBlockCollisions(entity, box,direction);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

    default boolean canPortalCollide(@Nullable Entity entity, Box box, Direction direction) {
        CustomBlockCollisionSpliteraror customBlockCollisionSpliterator = new CustomBlockCollisionSpliteraror(this, entity, box,direction, true);

        while(customBlockCollisionSpliterator.hasNext()) {
            if (!customBlockCollisionSpliterator.next().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box);

    @Nullable
    private VoxelShape getWorldBorderCollisions(Entity entity, Box box) {
        WorldBorder worldBorder = this.getWorldBorder();
        return worldBorder.canCollide(entity, box) ? worldBorder.asVoxelShape() : null;
    }

    default boolean isPortalSpaceEmpty(Box box,Direction direction) {
        return this.isPortalSpaceEmpty((Entity)null, box,direction);
    }

    default boolean isPortalSpaceEmpty(Entity entity,Direction direction) {
        return this.isPortalSpaceEmpty(entity, entity.getBoundingBox(),direction);
    }


    default boolean isPortalSpaceEmpty(@Nullable Entity entity, Box box,Direction direction) {
        for(VoxelShape voxelShape : this.getPortalBlockCollisions(entity, box,direction)) {
            if (!voxelShape.isEmpty()) {
                return false;
            }
        }

        if (!this.getEntityCollisions(entity, box).isEmpty()) {
            return false;
        } else if (entity == null) {
            return true;
        } else {
            VoxelShape voxelShape2 = this.getWorldBorderCollisions(entity, box);
            return voxelShape2 == null || !VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(box), BooleanBiFunction.AND);
        }
    }
}
