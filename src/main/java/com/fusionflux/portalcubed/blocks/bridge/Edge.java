package com.fusionflux.portalcubed.blocks.bridge;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

public enum Edge implements StringRepresentable {
    LEFT, RIGHT, DOWN, UP;

    private final Map<Direction, Direction> facingToAsDirection = new HashMap<>();

    public final String serialized = name().toLowerCase(Locale.ROOT);

    public Edge getClockwise() {
        return switch (this) {
            case LEFT -> UP;
            case DOWN -> LEFT;
            case RIGHT -> DOWN;
            case UP -> RIGHT;
        };
    }

    public Edge getCounterClockwise() {
        return switch (this) {
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
            case UP -> LEFT;
        };
    }

    public Edge getOpposite() {
        return getClockwise().getClockwise();
    }

    public Direction toDirection(Direction facing) {
        return facingToAsDirection.computeIfAbsent(facing, $ -> {
            Axis axis = facing.getAxis();
            Direction currentDirection = getDownOf(facing);
            Edge edge = DOWN;
            for (int i = 0; i < 4; i++) {
                if (edge == this)
                    return currentDirection;
                currentDirection = facing == Direction.UP ? currentDirection.getClockWise(axis) : currentDirection.getCounterClockWise(axis);
                edge = edge.getCounterClockwise();
            }
            throw new IllegalStateException();
        });
    }

    public Vec3 offsetTowards(Vec3 pos, Direction facing, double amount) {
        Direction direction = toDirection(facing);
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 offset = normal.scale(amount);
        return pos.add(offset);
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return serialized;
    }

    @Override
    public String toString() {
        return serialized;
    }

    public static Edge fromFacingAndSide(Direction facing, Direction side) {
        Axis axis = facing.getAxis();
        if (axis == side.getAxis())
            throw new IllegalArgumentException("facing and side cannot share an axis");
        // start with down, rotate around until side matches
        Direction currentSide = getDownOf(facing);
        Edge edge = DOWN;
        for (int i = 0; i < 4; i++) {
            if (currentSide == side)
                return edge;
            currentSide = facing == Direction.UP ? currentSide.getClockWise(axis) : currentSide.getCounterClockWise(axis);
            edge = edge.getCounterClockwise();
        }
        throw new IllegalStateException();
    }

    public static Edge teleport(Edge edge, Portal from, Direction fromFacing, Direction toFacing) {
        Vec3 edgeNormal = Vec3.atLowerCornerOf(edge.toDirection(fromFacing).getNormal());
        Vector3f otherEdgeNormal = PortalDirectionUtils.rotateVector(from, edgeNormal).toVector3f();
        Direction otherEdgeDirection = Direction.fromDelta(Math.round(otherEdgeNormal.x), Math.round(otherEdgeNormal.y), Math.round(otherEdgeNormal.z));
        if (otherEdgeDirection == null)
            otherEdgeDirection = Direction.getNearest(otherEdgeNormal.x, otherEdgeNormal.y, otherEdgeNormal.z);
        return fromFacingAndSide(toFacing, otherEdgeDirection);
    }

    private static Direction getDownOf(Direction facing) {
        if (facing.getAxis().isHorizontal()) {
            return Direction.DOWN;
        }
        return facing == Direction.UP ? Direction.NORTH : Direction.SOUTH;
    }
}
