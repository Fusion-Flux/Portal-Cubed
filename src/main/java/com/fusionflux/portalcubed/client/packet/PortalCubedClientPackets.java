package com.fusionflux.portalcubed.client.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.UUID;


public class PortalCubedClientPackets {
    public static final Identifier SPAWN_PACKET = new Identifier(PortalCubed.MODID, "spawn_packet");
    public static final Identifier FIZZLE_PACKET = new Identifier(PortalCubed.MODID, "fizzle");
    public static final Identifier HAND_SHAKE_PACKET = new Identifier(PortalCubed.MODID, "hand_shake");

    @Environment(EnvType.CLIENT)
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_PACKET, PortalCubedClientPackets::onEntitySpawn);
        ClientPlayNetworking.registerGlobalReceiver(FIZZLE_PACKET, PortalCubedClientPackets::onFizzle);
        ClientPlayNetworking.registerGlobalReceiver(HAND_SHAKE_PACKET, PortalCubedClientPackets::onHandShake);
    }

    @Environment(EnvType.CLIENT)
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
                if(world != null)
                world.addEntity(entityID, entity);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void onFizzle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        client.execute(() -> {
            assert client.world != null;
            if (client.world.getEntityById(entityId) instanceof CorePhysicsEntity physicsEntity) {
                assert client.player != null;
                if (client.player.getUuid().equals(physicsEntity.getHolderUUID())) {
                    physicsEntity.dropCube();
                }
                physicsEntity.startFizzlingProgress();
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void onHandShake(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.shakeStart = Util.getMeasuringTimeMs();
    }

}
