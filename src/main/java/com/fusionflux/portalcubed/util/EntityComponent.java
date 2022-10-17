package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    boolean gravityState = false;
    VoxelShape portalCutout = VoxelShapes.empty();
    //Vec3d velocity = Vec3d.ZERO;
    boolean hasTeleportationHappened = false;
    UUID cubeUUID = null;
    public List<UUID> portals = new ArrayList<>();
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
    public List<UUID> getPortals() {
        return portals;
    }

    @Override
    public void setPortals(List<UUID> portalUUIDs) {
        portals = portalUUIDs;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void addPortals(UUID portalUUID) {
        if(!portals.contains(portalUUID))
        portals.add(portalUUID);
        //portals = portalUUIDs;
        PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
    }

    @Override
    public void removePortals(UUID portalUUID) {
        //portals = portalUUIDs;
        if(!portals.contains(portalUUID))
        portals.remove(portalUUID);
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

        int size = tag.getInt("size");

        if(!portals.isEmpty())
            portals.clear();

        for (int i = 0; i < size; i++) {
            portals.add(tag.getUuid("portals"+i));
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

        int number = 0;
        for(UUID portal : portals) {
            tag.putUuid("portals" + number,portal);
            number++;
        }
        tag.putInt("size", portals.size());
        //tag.putUuid("cubeUUID",cubeUUID);
        tag.putBoolean("hasTpHappened",hasTeleportationHappened);
    }
}
