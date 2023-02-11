package com.fusionflux.portalcubed.util;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HolderComponent implements AutoSyncedComponent {

    private CorePhysicsEntity heldEntity = null;
    private Optional<UUID> heldEntityUUID = Optional.empty();

    private final PlayerEntity owner;

    HolderComponent(PlayerEntity owner) {
        this.owner = owner;
    }

    public boolean hold(CorePhysicsEntity entityToHold) {
        Objects.requireNonNull(entityToHold, "The entity to hold can not be null!");
        if (entityBeingHeld() == null && entityToHold.getFizzleProgress() == 0) {
            entityToHold.setHolderUUID(Optional.of(owner.getUuid()));
            this.heldEntity = entityToHold;
            this.heldEntity.setNoGravity(true);
            this.heldEntityUUID = Optional.of(entityToHold.getUuid());
            PortalCubedComponents.HOLDER_COMPONENT.sync(owner);
            return true;
        }
        return false;
    }

    public @Nullable CorePhysicsEntity entityBeingHeld() {
        if (heldEntity == null && heldEntityUUID.isPresent()) this.heldEntity = (CorePhysicsEntity) ((Accessors) this.owner.world).getEntity(heldEntityUUID.get());
        return this.heldEntity;
    }

    public boolean stopHolding() {
        if (this.heldEntity != null) {
            heldEntity.setHolderUUID(Optional.empty());
            this.heldEntity.setNoGravity(false);
            if (owner.world.isClient && !heldEntity.isRemoved()) {
                var buf = PacketByteBufs.create();
                buf.writeDouble(heldEntity.getPos().x);
                buf.writeDouble(heldEntity.getPos().y);
                buf.writeDouble(heldEntity.getPos().z);
                buf.writeDouble(heldEntity.lastPos.x);
                buf.writeDouble(heldEntity.lastPos.y);
                buf.writeDouble(heldEntity.lastPos.z);
                buf.writeFloat(heldEntity.bodyYaw);
                buf.writeUuid(heldEntity.getUuid());
                NetworkingSafetyWrapper.sendFromClient("cubeposupdate", buf);
            }

            this.heldEntityUUID = Optional.empty();
            PortalCubedComponents.HOLDER_COMPONENT.sync(owner);
            this.heldEntity = null;
            return true;
        }
        return false;
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("heldEntityUUID")) this.heldEntityUUID = Optional.of(tag.getUuid("heldEntityUUID"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        this.heldEntityUUID.ifPresent(value -> tag.putUuid("heldEntityUUID", value));
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.owner;
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        final var syncedHeldEntityUUID = TrackedDataHandlerRegistry.OPTIONAL_UUID.read(buf);
        if (heldEntity == null && syncedHeldEntityUUID.isPresent()) {
            hold((CorePhysicsEntity) ((Accessors) this.owner.world).getEntity(syncedHeldEntityUUID.get()));
        } else if (syncedHeldEntityUUID.isEmpty() && heldEntity != null) {
            stopHolding();
        }
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        TrackedDataHandlerRegistry.OPTIONAL_UUID.write(buf, this.heldEntityUUID);
    }

}
