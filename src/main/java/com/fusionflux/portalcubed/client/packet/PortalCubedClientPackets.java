package com.fusionflux.portalcubed.client.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.Fizzleable;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.fusionflux.portalcubed.listeners.ServerAnimatable;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;


public class PortalCubedClientPackets {
    public static final Identifier FIZZLE_PACKET = id("fizzle");
    public static final Identifier HAND_SHAKE_PACKET = id("hand_shake");
    public static final Identifier GEL_OVERLAY_PACKET = id("gel_overlay");
    public static final Identifier ROCKET_TURRET_UPDATE_PACKET = id("rocket_turret_update");
    public static final Identifier ENABLE_CFG = id("enable_cfg");
    public static final Identifier ENABLE_PORTAL_HUD = id("enable_portal_hud");
    public static final Identifier REFRESH_POS = id("refresh_pos");
    public static final Identifier SERVER_ANIMATE = id("server_animate");
    public static final Identifier SET_CUSTOM_FOG = id("set_custom_fog");

    @ClientOnly
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FIZZLE_PACKET, PortalCubedClientPackets::onFizzle);
        ClientPlayNetworking.registerGlobalReceiver(HAND_SHAKE_PACKET, PortalCubedClientPackets::onHandShake);
        ClientPlayNetworking.registerGlobalReceiver(GEL_OVERLAY_PACKET, PortalCubedClientPackets::onGelOverlay);
        ClientPlayNetworking.registerGlobalReceiver(ROCKET_TURRET_UPDATE_PACKET, PortalCubedClientPackets::onRocketTurretUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_CFG, PortalCubedClientPackets::onEnableCfg);
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_PORTAL_HUD, PortalCubedClientPackets::onEnablePortalHud);
        ClientPlayNetworking.registerGlobalReceiver(REFRESH_POS, PortalCubedClientPackets::onRefreshPos);
        ClientPlayNetworking.registerGlobalReceiver(SERVER_ANIMATE, PortalCubedClientPackets::onServerAnimate);
        ClientPlayNetworking.registerGlobalReceiver(SET_CUSTOM_FOG, PortalCubedClientPackets::onSetCustomFog);
    }

    @ClientOnly
    public static void onFizzle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        client.execute(() -> {
            assert client.world != null;
            final Entity entity = client.world.getEntityById(entityId);
            if (entity instanceof Fizzleable fizzleable) {
                fizzleable.startFizzlingProgress();
            }
            assert client.player != null;
            if (entity instanceof CorePhysicsEntity prop && client.player.getUuid().equals(prop.getHolderUUID().orElse(null))) {
                PortalCubedComponents.HOLDER_COMPONENT.get(client.player).stopHolding();
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });
    }

    @ClientOnly
    public static void onHandShake(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.shakeStart = Util.getMeasuringTimeMs();
    }

    @ClientOnly
    public static void onGelOverlay(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final Identifier blockId = buf.readIdentifier();
        PortalCubedClient.gelOverlayTimer = 0;
        PortalCubedClient.gelOverlayTexture = new Identifier(
            blockId.getNamespace(), "textures/block/" + blockId.getPath() + ".png"
        );
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

    @ClientOnly
    public static void onEnablePortalHud(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.setPortalHudMode(buf.readBoolean());
    }

    @ClientOnly
    public static void onRefreshPos(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        final double x = buf.readDouble();
        final double y = buf.readDouble();
        final double z = buf.readDouble();
        final float yaw = buf.readFloat();
        final float pitch = buf.readFloat();
        client.execute(() -> {
            assert client.world != null;
            final Entity entity = client.world.getEntityById(entityId);
            if (entity != null) {
                entity.refreshPositionAndAngles(x, y, z, yaw, pitch);
            }
        });
    }

    @ClientOnly
    public static void onServerAnimate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final BlockPos pos = buf.readBlockPos();
        final String animation = buf.readString();
        client.execute(() -> {
            assert client.world != null;
            final BlockEntity blockEntity = client.world.getBlockEntity(pos);
            AnimationState state = null;
            if (blockEntity instanceof ServerAnimatable serverAnimatable) {
                state = serverAnimatable.getAnimation(animation);
            }
            if (state != null) {
                // state can only ever be non-null if blockEntity instanceof ServerAnimatable
                state.restart(((ServerAnimatable)blockEntity).getAge());
            } else {
                PortalCubed.LOGGER.warn("Unknown animation from {}: {} for {}", SERVER_ANIMATE, animation, blockEntity);
            }
        });
    }

    @ClientOnly
    public static void onSetCustomFog(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        PortalCubedClient.customFog = FogSettings.decodeOptional(buf);
    }
}
