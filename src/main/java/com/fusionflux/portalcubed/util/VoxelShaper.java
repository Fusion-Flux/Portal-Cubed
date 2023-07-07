package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.fluids.ToxicGooFluid.Block;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Taken from Create, licensed under MIT: https://github.com/Creators-of-Create/Create/blob/mc1.18/dev/src/main/java/com/simibubi/create/foundation/utility/VoxelShaper.java
public class VoxelShaper {
    private static final Vec3 BLOCK_CENTER = new Vec3(8, 8, 8);

    private final Map<Direction, VoxelShape> shapes = new HashMap<>();

    public VoxelShape get(Direction direction) {
        return shapes.get(direction);
    }

    public VoxelShape get(Axis axis) {
        return shapes.get(axisAsFace(axis));
    }

    public static VoxelShaper forHorizontal(VoxelShape shape, Direction facing) {
        return forDirectionsWithRotation(shape, facing, Direction.Plane.HORIZONTAL, new HorizontalRotationValues());
    }

    public static VoxelShaper forHorizontalAxis(VoxelShape shape, Axis along) {
        return forDirectionsWithRotation(shape, axisAsFace(along), Arrays.asList(Direction.SOUTH, Direction.EAST),
                new HorizontalRotationValues());
    }

    public static VoxelShaper forDirectional(VoxelShape shape, Direction facing) {
        return forDirectionsWithRotation(shape, facing, Arrays.asList(Direction.values()), new DefaultRotationValues());
    }

    public static VoxelShaper forAxis(VoxelShape shape, Axis along) {
        return forDirectionsWithRotation(shape, axisAsFace(along),
                Arrays.asList(Direction.SOUTH, Direction.EAST, Direction.UP), new DefaultRotationValues());
    }

    public VoxelShaper withVerticalShapes(VoxelShape upShape) {
        shapes.put(Direction.UP, upShape);
        shapes.put(Direction.DOWN, rotatedCopy(upShape, new Vec3(180, 0, 0), BLOCK_CENTER));
        return this;
    }

    public VoxelShaper withShape(VoxelShape shape, Direction facing) {
        shapes.put(facing, shape);
        return this;
    }

    public static Direction axisAsFace(Axis axis) {
        return Direction.get(AxisDirection.POSITIVE, axis);
    }

    protected static float horizontalAngleFromDirection(Direction direction) {
        return (float) ((Math.max(direction.get2DDataValue(), 0) & 3) * 90);
    }

    protected static VoxelShaper forDirectionsWithRotation(VoxelShape shape, Direction facing,
                                                           Iterable<Direction> directions, Function<Direction, Vec3> rotationValues) {
        VoxelShaper voxelShaper = new VoxelShaper();
        for (Direction dir : directions) {
            voxelShaper.shapes.put(dir, rotate(shape, facing, dir, rotationValues));
        }
        return voxelShaper;
    }

    public static VoxelShape rotate(VoxelShape shape, Direction from, Direction to,
                                       Function<Direction, Vec3> usingValues) {
        if (from == to)
            return shape;

        return rotatedCopy(
            shape,
            usingValues.apply(from)
                .reverse()
                .add(usingValues.apply(to)),
            BLOCK_CENTER
        );
    }

    public static VoxelShape rotatedCopy(VoxelShape shape, Vec3 rotation, Vec3 origin) {
        if (rotation.equals(Vec3.ZERO))
            return shape;

        MutableObject<VoxelShape> result = new MutableObject<>(Shapes.empty());

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            Vec3 v1 = new Vec3(x1, y1, z1).scale(16)
                    .subtract(origin);
            Vec3 v2 = new Vec3(x2, y2, z2).scale(16)
                    .subtract(origin);

            v1 = rotate(v1, (float) rotation.x, Axis.X);
            v1 = rotate(v1, (float) rotation.y, Axis.Y);
            v1 = rotate(v1, (float) rotation.z, Axis.Z)
                    .add(origin);

            v2 = rotate(v2, (float) rotation.x, Axis.X);
            v2 = rotate(v2, (float) rotation.y, Axis.Y);
            v2 = rotate(v2, (float) rotation.z, Axis.Z)
                    .add(origin);

            VoxelShape rotated = blockBox(v1, v2);
            result.setValue(Shapes.or(result.getValue(), rotated));
        });

        return result.getValue();
    }

    protected static VoxelShape blockBox(Vec3 v1, Vec3 v2) {
        return Block.box(
                Math.min(v1.x, v2.x),
                Math.min(v1.y, v2.y),
                Math.min(v1.z, v2.z),
                Math.max(v1.x, v2.x),
                Math.max(v1.y, v2.y),
                Math.max(v1.z, v2.z)
        );
    }

    protected static class DefaultRotationValues implements Function<Direction, Vec3> {
        // assume facing up as the default rotation
        @Override
        public Vec3 apply(Direction direction) {
            return new Vec3(direction == Direction.UP ? 0 : (Direction.Plane.VERTICAL.test(direction) ? 180 : 90),
                    -horizontalAngleFromDirection(direction), 0);
        }
    }

    protected static class HorizontalRotationValues implements Function<Direction, Vec3> {
        @Override
        public Vec3 apply(Direction direction) {
            return new Vec3(0, -horizontalAngleFromDirection(direction), 0);
        }
    }

    // this method is from VecHelper: https://github.com/Creators-of-Create/Create/blob/39ef3da5df0fad2054d8b95f15b51b4199479774/src/main/java/com/simibubi/create/foundation/utility/VecHelper.java#L46
    public static Vec3 rotate(Vec3 vec, double deg, Axis axis) {
        if (deg == 0)
            return vec;
        if (vec == Vec3.ZERO)
            return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Mth.sin(angle);
        double cos = Mth.cos(angle);
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        if (axis == Axis.X)
            return new Vec3(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == Axis.Y)
            return new Vec3(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == Axis.Z)
            return new Vec3(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }

}
