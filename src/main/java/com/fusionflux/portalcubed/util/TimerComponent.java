package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TimerComponent implements PortalCubedComponent, AutoSyncedComponent {
    boolean gravityState = false;
    Box adjustedBox = new Box(0,0,0,0,0,0);
    Vec3d ommitedDirections;
    Entity entity;

    public TimerComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean getSwapGravity() {
        return gravityState;
    }

    @Override
    public void setSwapGravity(boolean gravityState) {
        this.gravityState = gravityState;
        PortalCubedComponents.GRAVITY_TIMER.sync(entity);
    }

    @Override
    public Box getPoralAdjustBoundingBox() {
        return adjustedBox;
    }

    @Override
    public void setPoralAdjustBoundingBox(Box entityBB) {
        adjustedBox = entityBB;
        PortalCubedComponents.GRAVITY_TIMER.sync(entity);
    }

    @Override
    public Vec3d getOmmitDirection() {
        return ommitedDirections;
    }

    @Override
    public void setOmmitDirection(Vec3d directions) {
        ommitedDirections = directions;
        PortalCubedComponents.GRAVITY_TIMER.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
