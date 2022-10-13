package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Objects;
import java.util.UUID;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    boolean gravityState = false;
    VoxelShape portalCutout = VoxelShapes.empty();
    //Vec3d velocity = Vec3d.ZERO;
    boolean hasTeleportationHappened = false;
    UUID cubeUUID = null;
    private final Entity entity;


    public EntityComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean getSwapGravity() {
        return gravityState;
    }

    @Override
    public void setSwapGravity(boolean gravityState) {
        this.gravityState = gravityState;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public VoxelShape getPortalCutout() {
        return this.portalCutout;
    }

    @Override
    public void setPortalCutout(VoxelShape portalCutout) {
            this.portalCutout = portalCutout;
    }

    @Override
    public boolean getHasTeleportationHappened() {
        return hasTeleportationHappened;
    }

    @Override
    public void setHasTeleportationHappened(boolean hasHappened) {
        hasTeleportationHappened=hasHappened;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void teleport(Vec3d teleportTo, Direction dira,Direction dirb) {

    }

    @Override
    public UUID getCubeUUID() {
        return cubeUUID;
    }

    @Override
    public void setCubeUUID(UUID CubeUUID) {
        cubeUUID = CubeUUID;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        //velocity = IPHelperDuplicate.getVec3d(tag, "ccaVelocity");
        String cubeString = tag.getString("cubeUUID");
        if(!Objects.equals(cubeString, "null")) {
            cubeUUID = UUID.fromString(tag.getString("cubeUUID"));
        }else{
            cubeUUID = null;
        }
        hasTeleportationHappened = tag.getBoolean("hasTpHappened");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        //IPHelperDuplicate.putVec3d(tag, "ccaVelocity", velocity);
        if(cubeUUID != null) {
            tag.putString("cubeUUID", cubeUUID.toString());
        }else{
            tag.putString("cubeUUID", "null");
        }
        //tag.putUuid("cubeUUID",cubeUUID);
        tag.putBoolean("hasTpHappened",hasTeleportationHappened);
    }
}
