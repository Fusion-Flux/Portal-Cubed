package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.ItemInHandRendererExt;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class HolderComponent implements AutoSyncedComponent {

    private CorePhysicsEntity heldEntity = null;
    private Optional<UUID> heldEntityUUID = Optional.empty();

    private final Player owner;

    HolderComponent(Player owner) {
        this.owner = owner;
    }

    public boolean hold(CorePhysicsEntity entityToHold) {
        Objects.requireNonNull(entityToHold, "The entity to hold can not be null!");
        if (entityBeingHeld() == null && !entityToHold.fizzling()) {
            entityToHold.setHolderUUID(Optional.of(owner.getUUID()));
            this.heldEntity = entityToHold;
            RayonIntegration.INSTANCE.setNoGravity(heldEntity, true);
            this.heldEntityUUID = Optional.of(entityToHold.getUUID());
            PortalCubedComponents.HOLDER_COMPONENT.sync(owner);
            return true;
        }
        return false;
    }

    public @Nullable CorePhysicsEntity entityBeingHeld() {
        if (heldEntity == null && heldEntityUUID.isPresent()) this.heldEntity = (CorePhysicsEntity) ((LevelExt) this.owner.level).getEntity(heldEntityUUID.get());
        return this.heldEntity;
    }

    public boolean stopHolding() {
        if (this.heldEntity != null) {
            heldEntity.setHolderUUID(Optional.empty());
            if (!heldEntity.fizzling()) {
                RayonIntegration.INSTANCE.setNoGravity(heldEntity, false);
            }
            if (owner.level.isClientSide && !heldEntity.isRemoved()) {
                var buf = PacketByteBufs.create();
                buf.writeDouble(heldEntity.position().x);
                buf.writeDouble(heldEntity.position().y);
                buf.writeDouble(heldEntity.position().z);
                buf.writeDouble(heldEntity.lastPos.x);
                buf.writeDouble(heldEntity.lastPos.y);
                buf.writeDouble(heldEntity.lastPos.z);
                buf.writeFloat(heldEntity.yBodyRot);
                buf.writeUUID(heldEntity.getUUID());
                NetworkingSafetyWrapper.sendFromClient("cube_pos_update", buf);
            }

            this.heldEntityUUID = Optional.empty();
            this.heldEntity = null;
            PortalCubedComponents.HOLDER_COMPONENT.sync(owner);
            return true;
        }
        return false;
    }


    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("heldEntityUUID")) this.heldEntityUUID = Optional.of(tag.getUUID("heldEntityUUID"));
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        this.heldEntityUUID.ifPresent(value -> tag.putUUID("heldEntityUUID", value));
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return player == this.owner;
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        final var syncedHeldEntityUUID = EntityDataSerializers.OPTIONAL_UUID.read(buf);
        if (heldEntity == null && syncedHeldEntityUUID.isPresent()) {
            hold((CorePhysicsEntity) ((LevelExt) this.owner.level).getEntity(syncedHeldEntityUUID.get()));
        } else if (syncedHeldEntityUUID.isEmpty() && heldEntity != null) {
            stopHolding();
        }
        handHideRefresh();
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        EntityDataSerializers.OPTIONAL_UUID.write(buf, this.heldEntityUUID);
    }

    private void handHideRefresh() {
        if (owner.getMainHandItem().is(PortalCubedItems.HOLDS_OBJECT)) return;
        owner.resetAttackStrengthTicker();
        ((ItemInHandRendererExt)Minecraft.getInstance().gameRenderer.itemInHandRenderer).startHandFaker();
    }

}
