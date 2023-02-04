package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    VoxelShape portalCutout = VoxelShapes.empty();
    boolean hasTeleportationHappened = false;
    public Set<UUID> portals = new HashSet<>();
    private final Entity entity;


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
        int size = tag.getInt("size");

        if(!portals.isEmpty())
            portals.clear();

        for (int i = 0; i < size; i++) {
            portals.add(tag.getUuid("portals"+i));
        }

        hasTeleportationHappened = tag.getBoolean("hasTpHappened");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        int number = 0;
        for(UUID portal : portals) {
            tag.putUuid("portals" + number,portal);
            number++;
        }
        tag.putInt("size", portals.size());
        tag.putBoolean("hasTpHappened",hasTeleportationHappened);
    }
}
