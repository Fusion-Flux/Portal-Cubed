package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    VoxelShape portalCutout = VoxelShapes.empty();
    boolean hasTeleportationHappened = false;
    UUID cubeUUID = null;
    public final Set<UUID> portals = new HashSet<>();
    private final Entity entity;

    boolean wasInfiniteFalling;

    boolean canFireGel;

    Vec3d teleportVelocity = Vec3d.ZERO;

    Vec3d serverVelForGel = Vec3d.ZERO;

    public EntityComponent(Entity entity) {
        this.entity = entity;
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
    public boolean getWasInfiniteFalling() {
        return wasInfiniteFalling;
    }

    @Override
    public void setWasInfiniteFalling(boolean infFall) {
        wasInfiniteFalling = infFall;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public Vec3d getVelocityUpdateAfterTeleport() {
        return teleportVelocity;
    }

    @Override
    public void setVelocityUpdateAfterTeleport(Vec3d velocity) {
        teleportVelocity = velocity;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }



    @Override
    public boolean getCanFireGel() {
        return canFireGel;
    }

    @Override
    public void setCanFireGel(boolean canGel) {
        canFireGel = canGel;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public Vec3d getServerVelForGel() {
        return serverVelForGel;
    }

    @Override
    public void setServerVelForGel(Vec3d velocity) {
        serverVelForGel = velocity;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
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
    public Set<UUID> getPortals() {
        return portals;
    }

    @Override
    public void addPortals(UUID portalUUID) {
        portals.add(portalUUID);
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void removePortals(UUID portalUUID) {
        portals.remove(portalUUID);
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        String cubeString = tag.getString("cubeUUID");
        if(!Objects.equals(cubeString, "null")) {
            cubeUUID = UUID.fromString(tag.getString("cubeUUID"));
        }else{
            cubeUUID = null;
        }

        int size = tag.getInt("size");

        if(!portals.isEmpty())
            portals.clear();

        for (int i = 0; i < size; i++) {
            portals.add(tag.getUuid("portals"+i));
        }

        hasTeleportationHappened = tag.getBoolean("hasTpHappened");

        this.setVelocityUpdateAfterTeleport(IPHelperDuplicate.getVec3d(tag, "velocity"));

        setWasInfiniteFalling(tag.getBoolean("wasInfiniteFalling"));

        this.setServerVelForGel(IPHelperDuplicate.getVec3d(tag, "gelVelocity"));

        setCanFireGel(tag.getBoolean("canFireGel"));
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if(cubeUUID != null) {
            tag.putString("cubeUUID", cubeUUID.toString());
        }else{
            tag.putString("cubeUUID", "null");
        }

        int number = 0;
        for(UUID portal : portals) {
            tag.putUuid("portals" + number,portal);
            number++;
        }
        tag.putInt("size", portals.size());
        tag.putBoolean("hasTpHappened",hasTeleportationHappened);

        IPHelperDuplicate.putVec3d(tag, "velocity", this.getVelocityUpdateAfterTeleport());

        tag.putBoolean("wasInfiniteFalling",wasInfiniteFalling);

        IPHelperDuplicate.putVec3d(tag, "gelVelocity", this.getServerVelForGel());

        tag.putBoolean("canFireGel",canFireGel);
    }
}
