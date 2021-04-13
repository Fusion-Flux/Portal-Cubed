package com.fusionflux.thinkingwithportatos.client.packet;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ThinkingWithPortatosClientPackets {
    public static final Identifier SPAWN_PACKET = new Identifier(ThinkingWithPortatos.MODID, "spawn_packet");
    public static final Identifier GRAB_PACKET = new Identifier(ThinkingWithPortatos.MODID, "grab_packet");
    public static final Identifier UNGRAB_PACKET = new Identifier(ThinkingWithPortatos.MODID, "ungrab_packet");

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_PACKET, ThinkingWithPortatosClientPackets::onEntitySpawn);
        ClientPlayNetworking.registerGlobalReceiver(GRAB_PACKET, ThinkingWithPortatosClientPackets::onGrab);
        ClientPlayNetworking.registerGlobalReceiver(UNGRAB_PACKET, ThinkingWithPortatosClientPackets::onUngrab);
    }

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
                entity.updateTrackedPosition(x, y, z);
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);
                assert world != null;
                world.addEntity(entityID, entity);
            }
        });
    }

    public static void onGrab(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int grabberId = buf.readInt();
        int entityId = buf.readInt();

        client.execute(() -> {
            if (client.world != null) {
                Entity grabber = client.world.getEntityById(grabberId);
                Entity entity = client.world.getEntityById(entityId);

                if (grabber instanceof PlayerEntity) {
                    ThinkingWithPortatos.getBodyGrabbingManager(true).tryGrab((PlayerEntity) grabber, entity);
                }
            }
        });
    }

    public static void onUngrab(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int grabberId = buf.readInt();

        client.execute(() -> {
            if (client.world != null) {
                Entity grabber = client.world.getEntityById(grabberId);

                if (grabber instanceof PlayerEntity) {
                    ThinkingWithPortatos.getBodyGrabbingManager(true).tryUngrab((PlayerEntity) grabber);
                }
            }
        });
    }
}