package com.fusionflux.portalcubed.blocks.bridge;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

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
}
