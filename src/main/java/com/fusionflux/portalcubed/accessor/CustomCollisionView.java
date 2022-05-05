package com.fusionflux.portalcubed.accessor;

import com.google.common.collect.Iterables;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public interface CustomCollisionView extends BlockView {

    @Nullable
    BlockView getChunkAsView(int chunkX, int chunkZ);

    WorldBorder getWorldBorder();

    default Iterable<VoxelShape> getPortalBlockCollisions(@Nullable Entity entity, Box box, Vec3d direction) {
        return () -> new CustomBlockCollisionSpliteraror(this, entity, box,direction);
    }

    default Iterable<VoxelShape> getPortalCollisions(@Nullable Entity entity, Box box,Vec3d direction) {
        List<VoxelShape> list = this.getEntityCollisions(entity, box);
        Iterable<VoxelShape> iterable = this.getPortalBlockCollisions(entity, box,direction);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

    default boolean canPortalCollide(@Nullable Entity entity, Box box, Vec3d direction) {
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

    default boolean isPortalSpaceEmpty(Box box,Vec3d direction) {
        return this.isPortalSpaceEmpty((Entity)null, box,direction);
    }

    default boolean isPortalSpaceEmpty(Entity entity,Vec3d direction) {
        return this.isPortalSpaceEmpty(entity, entity.getBoundingBox(),direction);
    }


    default boolean isPortalSpaceEmpty(@Nullable Entity entity, Box box,Vec3d direction) {
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

    default Optional<Vec3d> findClosestPortalCollision(@Nullable Entity entity, VoxelShape shape, Vec3d target, double x, double y, double z,Vec3d direction) {
        if (shape.isEmpty()) {
            return Optional.empty();
        } else {
            Box box = shape.getBoundingBox().expand(x, y, z);
            VoxelShape voxelShape = (VoxelShape) StreamSupport.stream(this.getPortalBlockCollisions(entity, box,direction).spliterator(), false)
                    .filter(voxelShapex -> this.getWorldBorder() == null || this.getWorldBorder().contains(voxelShapex.getBoundingBox()))
                    .flatMap(voxelShapex -> voxelShapex.getBoundingBoxes().stream())
                    .map(boxx -> boxx.expand(x / 2.0, y / 2.0, z / 2.0))
                    .map(VoxelShapes::cuboid)
                    .reduce(VoxelShapes.empty(), VoxelShapes::union);
            VoxelShape voxelShape2 = VoxelShapes.combineAndSimplify(shape, voxelShape, BooleanBiFunction.ONLY_FIRST);
            return voxelShape2.getClosestPointTo(target);
        }
    }
}
