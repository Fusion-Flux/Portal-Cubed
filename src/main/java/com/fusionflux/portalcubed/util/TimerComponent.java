package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class TimerComponent implements PortalCubedComponent, AutoSyncedComponent {
    boolean gravityState = false;
    Vec3d ommitedDirections = Vec3d.ZERO;
    UUID cubeUUID = null;
    private final Entity entity;

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
    public Vec3d getOmmitDirection() {
        return ommitedDirections;
    }

    @Override
    public void setOmmitDirection(Vec3d directions) {
        ommitedDirections = directions;
        PortalCubedComponents.GRAVITY_TIMER.sync(entity);
    }


    @Override
    public UUID getCubeUUID() {
        return cubeUUID;
    }

    @Override
    public void setCubeUUID(UUID CubeUUID) {
        cubeUUID = CubeUUID;
        PortalCubedComponents.GRAVITY_TIMER.sync(entity);
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.setOmmitDirection(IPHelperDuplicate.getVec3d(tag, "ommitDirection"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        IPHelperDuplicate.putVec3d(tag, "ommitDirection", this.getOmmitDirection());
    }
}
