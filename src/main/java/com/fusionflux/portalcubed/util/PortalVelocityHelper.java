package com.fusionflux.portalcubed.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PortalVelocityHelper {
    public static Vec3d rotateVelocity(Vec3d velocity, Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> velocity.multiply(1, 1, -1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(1, 1, -1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getX(), -velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(-1, 1, 1);
                case NORTH -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case SOUTH -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(velocity.getY(), -velocity.getX(), velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                default -> velocity;
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(-1, 1, 1);
                case NORTH -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), velocity.getZ());
                default -> velocity;
            };
            case UP -> switch (exitDirection) {
                case UP -> velocity.multiply(1, -1, 1);
                case NORTH -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), velocity.getZ(), -velocity.getY());
                case EAST -> new Vec3d(-velocity.getY(), velocity.getX(), velocity.getZ());
                case WEST -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                default -> velocity;
            };
            case DOWN -> switch (exitDirection) {
                case DOWN -> velocity.multiply(1, -1, 1);
                case NORTH -> new Vec3d(velocity.getX(), velocity.getZ(), -velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                case EAST -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                case WEST -> new Vec3d(-velocity.getY(), velocity.getX(), velocity.getZ());
                default -> velocity;
            };
        };
    }


    public static Vec3d rotatePosition(Vec3d velocity, double entityHeight, Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> velocity.multiply(1, 1, -1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(1, 1, -1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getX(), -velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(velocity.getX(), velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(-1, 1, 1);
                case NORTH -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case SOUTH -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(velocity.getY(), -velocity.getX(), velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                default -> velocity;
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(-1, 1, 1);
                case NORTH -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), velocity.getZ());
                default -> velocity;
            };
            case UP -> switch (exitDirection) {
                case UP -> velocity.multiply(1, -1, 1);
                case NORTH -> new Vec3d(velocity.getX(), entityHeight, velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), entityHeight, -velocity.getY());
                case EAST -> new Vec3d(-velocity.getY(), entityHeight, velocity.getZ());
                case WEST -> new Vec3d(velocity.getY(), entityHeight, velocity.getZ());
                default -> velocity;
            };
            case DOWN -> switch (exitDirection) {
                case DOWN -> velocity.multiply(1, -1, 1);
                case NORTH -> new Vec3d(velocity.getX(), entityHeight, -velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), entityHeight, velocity.getY());
                case EAST -> new Vec3d(velocity.getY(), entityHeight, velocity.getZ());
                case WEST -> new Vec3d(-velocity.getY(), entityHeight, velocity.getZ());
                default -> velocity;
            };
        };
    }

    public static float yawAddition(Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> 180;
                case EAST -> -90;
                case WEST -> 90;
                default -> 0;
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> 180;
                case EAST -> 90;
                case WEST -> -90;
                default -> 0;
            };
            case EAST -> switch (exitDirection) {
                case EAST -> 180;
                case NORTH -> 90;
                case SOUTH -> -90;
                default -> 0;
            };
            case WEST -> switch (exitDirection) {
                case WEST -> 180;
                case NORTH -> -90;
                case SOUTH -> 90;
                default -> 0;
            };
            default -> 0;
        };
    }

}
