package com.fusionflux.portalcubed.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class GeneralUtil {
    /**
     * @author maximum
     */
    public static VoxelShape rotate(VoxelShape shape, Direction dir) {
        List<VoxelShape> shapes = new ArrayList<>();

        shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(switch (dir) {
            case WEST -> VoxelShapes.cuboid(z1, y1, x1, z2, y2, x2);
            case SOUTH -> VoxelShapes.cuboid(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
            case EAST -> VoxelShapes.cuboid(1 - z2, y1, 1 - x2, 1 - z1, y2, 1 - x1);
            default -> VoxelShapes.cuboid(x1, y1, z1, x2, y2, z2);
        }));

        return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[0]));
    }

    public static double calculateVelocity(double x, double y, double a, double g) {
        a = Math.toRadians(a);
        return sqrt(x * x * g / (2 * cos(-a) * (x * sin(-a) + y * cos(-a))));
    }
}
