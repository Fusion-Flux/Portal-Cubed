package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TimerComponent implements PortalCubedComponent {
    boolean gravityState = false;
    Box adjustedBox = new Box(0,0,0,0,0,0);
    Vec3d ommitedDirections;

    @Override
    public boolean getSwapGravity() {
        return gravityState;
    }

    @Override
    public void setSwapGravity(boolean gravityState) {
        this.gravityState = gravityState;
    }

    @Override
    public Box getPoralAdjustBoundingBox() {
        return adjustedBox;
    }

    @Override
    public void setPoralAdjustBoundingBox(Box entityBB) {
        adjustedBox = entityBB;
    }

    @Override
    public Vec3d getOmmitDirection() {
        return ommitedDirections;
    }

    @Override
    public void setOmmitDirection(Vec3d directions) {
        ommitedDirections = directions;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
