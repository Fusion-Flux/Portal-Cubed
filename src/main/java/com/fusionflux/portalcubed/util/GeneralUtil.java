package com.fusionflux.portalcubed.util;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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

    // Code based off of code from ChatGPT
    public static Vec3d calculatePerpendicularVector(Vec3d lineStart, Vec3d lineEnd, Vec3d point) {
        // Calculate direction vector of line
        final Vec3d d = lineEnd.subtract(lineStart);

        // Calculate vector from point to line
        final Vec3d p = point.subtract(lineStart);

        // Calculate projection of point vector onto line vector
        final double t = p.dotProduct(d) / d.dotProduct(d);

        // Calculate perpendicular vector
        return p.subtract(d.multiply(t));
    }

    public static boolean targetsEqual(HitResult a, HitResult b) {
        return a.getType() == b.getType() && switch (a.getType()) {
            case MISS -> true;
            case BLOCK -> ((BlockHitResult)a).getBlockPos().equals(((BlockHitResult)b).getBlockPos());
            case ENTITY -> ((EntityHitResult)a).getEntity() == ((EntityHitResult)b).getEntity();
        };
    }

    // Based on https://forum.unity.com/threads/how-do-i-find-the-closest-point-on-a-line.340058/
    public static Vec3d nearestPointOnLine(Vec3d linePnt, Vec3d lineDir, Vec3d pnt) {
        lineDir = lineDir.normalize();
        final Vec3d v = pnt.subtract(linePnt);
        final double d = v.dotProduct(lineDir);
        return linePnt.add(lineDir.multiply(d));
    }
}
