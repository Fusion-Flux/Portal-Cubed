package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.UUID;

public class EntityComponent implements PortalCubedComponent, AutoSyncedComponent {
    boolean gravityState = false;
    VoxelShape portalCutout = VoxelShapes.empty();
    Vec3d velocity = Vec3d.ZERO;
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
    public void teleport(Vec3d teleportTo, Direction dira,Direction dirb) {

    }

    @Override
    public Vec3d getVelocity() {
        return this.velocity;
    }

    @Override
    public void setVelocity(Vec3d velocity) {
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
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
    }
}
