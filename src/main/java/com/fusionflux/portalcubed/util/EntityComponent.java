package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    VoxelShape portalCutout = Shapes.empty();
    VoxelShape crossPortalCollision = Shapes.empty();
    boolean hasTeleportationHappened = false;
    public Set<UUID> portals = new HashSet<>();
    private final Entity entity;

    boolean wasInfiniteFalling;

    boolean canFireGel;

    Vec3 teleportVelocity = Vec3.ZERO;

    Vec3 serverVelForGel = Vec3.ZERO;

    @Nullable
    private BlockPos launcher;

    public EntityComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public VoxelShape getPortalCutout() {
        return this.portalCutout;
    }

    @Override
    public void setPortalCutout(VoxelShape cutout) {
        this.portalCutout = cutout;
    }

    @Override
    public VoxelShape getCrossPortalCollision() {
        return this.crossPortalCollision;
    }

    @Override
    public void setCrossPortalCollision(VoxelShape collision) {
        this.crossPortalCollision = collision;
    }

    @Override
    public boolean getHasTeleportationHappened() {
        return hasTeleportationHappened;
    }

    @Override
    public void setHasTeleportationHappened(boolean hasHappened) {
        if (hasTeleportationHappened != hasHappened) {
            hasTeleportationHappened = hasHappened;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public boolean getWasInfiniteFalling() {
        return wasInfiniteFalling;
    }

    @Override
    public void setWasInfiniteFalling(boolean infFall) {
        if (wasInfiniteFalling != infFall) {
            wasInfiniteFalling = infFall;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public Vec3 getVelocityUpdateAfterTeleport() {
        return teleportVelocity;
    }

    @Override
    public void setVelocityUpdateAfterTeleport(Vec3 velocity) {
        if (!teleportVelocity.equals(velocity)) {
            teleportVelocity = velocity;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }



    @Override
    public boolean getCanFireGel() {
        return canFireGel;
    }

    @Override
    public void setCanFireGel(boolean canGel) {
        if (canFireGel != canGel) {
            canFireGel = canGel;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public Vec3 getServerVelForGel() {
        return serverVelForGel;
    }

    @Override
    public void setServerVelForGel(Vec3 velocity) {
        if (!serverVelForGel.equals(velocity)) {
            serverVelForGel = velocity;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public Set<UUID> getPortals() {
        return portals;
    }

    @Override
    public void addPortals(UUID portalUUID) {
        if (portals.add(portalUUID)) {
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public void removePortals(UUID portalUUID) {
        if (portals.remove(portalUUID)) {
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Nullable
    @Override
    public BlockPos getLauncher() {
        return launcher;
    }

    @Override
    public void setLauncher(@Nullable BlockPos launcher) {
        if (!Objects.equals(this.launcher, launcher)) {
            this.launcher = launcher;
            PortalCubedComponents.ENTITY_COMPONENT.sync(entity);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        int size = tag.getInt("size");

        if (!portals.isEmpty())
            portals.clear();

        for (int i = 0; i < size; i++) {
            portals.add(tag.getUUID("portals" + i));
        }

        hasTeleportationHappened = tag.getBoolean("hasTpHappened");

        this.setVelocityUpdateAfterTeleport(NbtHelper.getVec3d(tag, "velocity"));

        setWasInfiniteFalling(tag.getBoolean("wasInfiniteFalling"));

        this.setServerVelForGel(NbtHelper.getVec3d(tag, "gelVelocity"));

        setCanFireGel(tag.getBoolean("canFireGel"));

        setLauncher(NbtHelper.readNullableBlockPos(tag, "launcher"));
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        int number = 0;
        for (UUID portal : portals) {
            tag.putUUID("portals" + number, portal);
            number++;
        }
        tag.putInt("size", portals.size());
        tag.putBoolean("hasTpHappened", hasTeleportationHappened);

        NbtHelper.putVec3d(tag, "velocity", this.getVelocityUpdateAfterTeleport());

        tag.putBoolean("wasInfiniteFalling", wasInfiniteFalling);

        NbtHelper.putVec3d(tag, "gelVelocity", this.getServerVelForGel());

        tag.putBoolean("canFireGel", canFireGel);

        if (launcher != null) {
            tag.put("launcher", NbtUtils.writeBlockPos(launcher));
        }
    }
}
