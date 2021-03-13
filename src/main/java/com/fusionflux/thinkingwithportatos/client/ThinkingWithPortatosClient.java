package com.fusionflux.thinkingwithportatos.client;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.client.render.CubeEntityRenderer;
import com.fusionflux.thinkingwithportatos.entity.CompanionCubeEntity;
import com.fusionflux.thinkingwithportatos.entity.CubeEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class ThinkingWithPortatosClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerClientPacketReceivers();
        registerEntityRenderers();
        ThinkingWithPortatosBlocks.registerRenderLayers();
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.CUBE, (dispatcher, context) -> new CubeEntityRenderer(dispatcher, false));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.COMPANION_CUBE, (dispatcher, context) -> new CubeEntityRenderer(dispatcher, true));
    }

    private void registerClientPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(CubeEntity.SPAWN_PACKET, entitySpawnPacket());
        ClientPlayNetworking.registerGlobalReceiver(CompanionCubeEntity.SPAWN_PACKET, entitySpawnPacket());
    }

    private static ClientPlayNetworking.PlayChannelHandler entitySpawnPacket() {
        return (client, handler, packet, responder) -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(packet.readVarInt());
            UUID entityUUID = packet.readUuid();
            int entityID = packet.readVarInt();
            double x = packet.readDouble();
            double y = packet.readDouble();
            double z = packet.readDouble();
            float pitch = (packet.readByte() * 360) / 256.0F;
            float yaw = (packet.readByte() * 360) / 256.0F;
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
        };
    }
}
