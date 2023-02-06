package com.fusionflux.portalcubed.client.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.util.PortalCubedComponents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.UUID;


public class PortalCubedClientPackets {
    public static final Identifier SPAWN_PACKET = new Identifier(PortalCubed.MOD_ID, "spawn_packet");
    public static final Identifier FIZZLE_PACKET = new Identifier(PortalCubed.MOD_ID, "fizzle");
    public static final Identifier HAND_SHAKE_PACKET = new Identifier(PortalCubed.MOD_ID, "hand_shake");
    public static final Identifier GEL_OVERLAY_PACKET = new Identifier(PortalCubed.MOD_ID, "gel_overlay");
    public static final Identifier ROCKET_TURRET_UPDATE_PACKET = new Identifier(PortalCubed.MOD_ID, "rocket_turret_update");

    @ClientOnly
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_PACKET, PortalCubedClientPackets::onEntitySpawn);
        ClientPlayNetworking.registerGlobalReceiver(FIZZLE_PACKET, PortalCubedClientPackets::onFizzle);
        ClientPlayNetworking.registerGlobalReceiver(HAND_SHAKE_PACKET, PortalCubedClientPackets::onHandShake);
        ClientPlayNetworking.registerGlobalReceiver(GEL_OVERLAY_PACKET, PortalCubedClientPackets::onGelOverlay);
        ClientPlayNetworking.registerGlobalReceiver(ROCKET_TURRET_UPDATE_PACKET, PortalCubedClientPackets::onRocketTurretUpdate);
    }

    @ClientOnly
    public static void onEntitySpawn(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());
        UUID entityUUID = buf.readUuid();
        int entityID = buf.readVarInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float pitch = (buf.readByte() * 360) / 256.0F;
        float yaw = (buf.readByte() * 360) / 256.0F;
        ClientWorld world = MinecraftClient.getInstance().world;
        Entity entity = type.create(world);
        client.execute(() -> {
            if (entity != null) {
                entity.updatePosition(x, y, z);
                entity.syncPacketPositionCodec(x, y, z);
                entity.setPitch(pitch);
                entity.setYaw(yaw);
                entity.setId(entityID);
                entity.setUuid(entityUUID);
                assert world != null;
                world.addEntity(entityID, entity);
            }
        });
    }

    @ClientOnly
    public static void onFizzle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        client.execute(() -> {
            if (client.world.getEntityById(entityId) instanceof CorePhysicsEntity physicsEntity) {
                physicsEntity.getHolderUUID().ifPresent(value -> {
                    if (client.player.getUuid().equals(value)) PortalCubedComponents.HOLDER_COMPONENT.get(client.player).stopHolding();
                });
                physicsEntity.startFizzlingProgress();
            }
        });
    }

    @ClientOnly
    public static void onHandShake(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.shakeStart = Util.getMeasuringTimeMs();
    }

    @ClientOnly
    public static void onGelOverlay(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        // TODO: Implement
    }

    @ClientOnly
    @SuppressWarnings("unchecked")
    public static void onRocketTurretUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final BlockPos pos = buf.readBlockPos();
        final int mode = buf.readByte();
        final Object arg = switch (mode) {
            case RocketTurretBlockEntity.UPDATE_ANGLE -> new Pair<>(buf.readFloat(), buf.readFloat());
            case RocketTurretBlockEntity.UPDATE_LOCKED_TICKS -> buf.readVarInt();
            default -> {
                PortalCubed.LOGGER.error("Malformed rocket_turret_update packet. Unknown mode {}.", mode);
                yield null;
            }
        };
        if (arg == null) return;
        client.execute(() -> handler.getWorld()
            .getBlockEntity(pos, PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY)
            .ifPresentOrElse(
                entity -> {
                    switch (mode) {
                        case RocketTurretBlockEntity.UPDATE_ANGLE -> entity.setAngle((Pair<Float, Float>)arg);
                        case RocketTurretBlockEntity.UPDATE_LOCKED_TICKS -> entity.setLockedTicks((int)arg);
                    }
                },
                () -> PortalCubed.LOGGER.warn("Received rocket_turret_update for unloaded rocket turret at {}", pos)
            )
        );
    }

}
