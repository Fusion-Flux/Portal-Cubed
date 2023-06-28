package com.fusionflux.portalcubed.blocks.bridge;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;

public enum Edge implements StringRepresentable {
    LEFT, RIGHT, DOWN, UP;

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

    private static Direction getDownOf(Direction facing) {
        if (facing.getAxis().isHorizontal()) {
            return Direction.DOWN;
        }
        return facing == Direction.UP ? Direction.NORTH : Direction.SOUTH;
    }
}
