package com.fusionflux.portalcubed.client.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.entity.Fizzleable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;


public class PortalCubedClientPackets {
    public static final Identifier FIZZLE_PACKET = id("fizzle");
    public static final Identifier HAND_SHAKE_PACKET = id("hand_shake");
    public static final Identifier GEL_OVERLAY_PACKET = id("gel_overlay");
    public static final Identifier ROCKET_TURRET_UPDATE_PACKET = id("rocket_turret_update");
    public static final Identifier ENABLE_CFG = id("enable_cfg");

    @ClientOnly
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FIZZLE_PACKET, PortalCubedClientPackets::onFizzle);
        ClientPlayNetworking.registerGlobalReceiver(HAND_SHAKE_PACKET, PortalCubedClientPackets::onHandShake);
        ClientPlayNetworking.registerGlobalReceiver(GEL_OVERLAY_PACKET, PortalCubedClientPackets::onGelOverlay);
        ClientPlayNetworking.registerGlobalReceiver(ROCKET_TURRET_UPDATE_PACKET, PortalCubedClientPackets::onRocketTurretUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_CFG, PortalCubedClientPackets::onEnableCfg);
    }

    @ClientOnly
    public static void onFizzle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        client.execute(() -> {
            assert client.world != null;
            if (client.world.getEntityById(entityId) instanceof Fizzleable fizzleable) {
                fizzleable.startFizzlingProgress();
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
                () -> PortalCubed.LOGGER.warn("Received rocket_turret_update for unloaded rocket turret at {}.", pos)
            )
        );
    }

    @ClientOnly
    public static void onEnableCfg(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.allowCfg = buf.readBoolean();
    }

}
