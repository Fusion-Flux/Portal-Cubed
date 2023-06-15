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
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;


public class PortalCubedClientPackets {
    public static final ResourceLocation FIZZLE_PACKET = id("fizzle");
    public static final ResourceLocation HAND_SHAKE_PACKET = id("hand_shake");
    public static final ResourceLocation GEL_OVERLAY_PACKET = id("gel_overlay");
    public static final ResourceLocation ROCKET_TURRET_UPDATE_PACKET = id("rocket_turret_update");
    public static final ResourceLocation ENABLE_CFG = id("enable_cfg");
    public static final ResourceLocation ENABLE_PORTAL_HUD = id("enable_portal_hud");
    public static final ResourceLocation REFRESH_POS = id("refresh_pos");
    public static final ResourceLocation SERVER_ANIMATE = id("server_animate");
    public static final ResourceLocation SET_CUSTOM_FOG = id("set_custom_fog");
    public static final ResourceLocation SET_CAMERA_INTERPOLATE = id("set_roll");

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
        ClientPlayNetworking.registerGlobalReceiver(SET_CAMERA_INTERPOLATE, PortalCubedClientPackets::onSetCameraInterpolate);
    }

    @ClientOnly
    public static void onFizzle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        client.execute(() -> {
            assert client.level != null;
            final Entity entity = client.level.getEntity(entityId);
            if (entity instanceof Fizzleable fizzleable) {
                fizzleable.startFizzlingProgress();
            }
            assert client.player != null;
            if (entity instanceof CorePhysicsEntity prop && client.player.getUUID().equals(prop.getHolderUUID().orElse(null))) {
                PortalCubedComponents.HOLDER_COMPONENT.get(client.player).stopHolding();
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });
    }

    @ClientOnly
    public static void onHandShake(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        PortalCubedClient.shakeStart = Util.getMillis();
    }

    @ClientOnly
    public static void onGelOverlay(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final ResourceLocation blockId = buf.readResourceLocation();
        PortalCubedClient.gelOverlayTimer = 0;
        PortalCubedClient.gelOverlayTexture = new ResourceLocation(
            blockId.getNamespace(), "textures/block/" + blockId.getPath() + ".png"
        );
    }

    @ClientOnly
    @SuppressWarnings("unchecked")
    public static void onRocketTurretUpdate(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final BlockPos pos = buf.readBlockPos();
        final int mode = buf.readByte();
        final Object arg = switch (mode) {
            case RocketTurretBlockEntity.UPDATE_ANGLE -> new Tuple<>(buf.readFloat(), buf.readFloat());
            case RocketTurretBlockEntity.UPDATE_LOCKED_TICKS -> buf.readVarInt();
            default -> {
                PortalCubed.LOGGER.error("Malformed rocket_turret_update packet. Unknown mode {}.", mode);
                yield null;
            }
        };
        if (arg == null) return;
        client.execute(() -> handler.getLevel()
            .getBlockEntity(pos, PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY)
            .ifPresentOrElse(
                entity -> {
                    switch (mode) {
                        case RocketTurretBlockEntity.UPDATE_ANGLE -> entity.setAngle((Tuple<Float, Float>)arg);
                        case RocketTurretBlockEntity.UPDATE_LOCKED_TICKS -> entity.setLockedTicks((int)arg);
                    }
                },
                () -> PortalCubed.LOGGER.warn("Received rocket_turret_update for unloaded rocket turret at {}.", pos)
            )
        );
    }

    @ClientOnly
    public static void onEnableCfg(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        PortalCubedClient.allowCfg = buf.readBoolean();
    }

    @ClientOnly
    public static void onEnablePortalHud(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        PortalCubedClient.setPortalHudMode(buf.readBoolean());
    }

    @ClientOnly
    public static void onRefreshPos(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final int entityId = buf.readVarInt();
        final double x = buf.readDouble();
        final double y = buf.readDouble();
        final double z = buf.readDouble();
        final float yaw = buf.readFloat();
        final float pitch = buf.readFloat();
        client.execute(() -> {
            assert client.level != null;
            final Entity entity = client.level.getEntity(entityId);
            if (entity != null) {
                entity.moveTo(x, y, z, yaw, pitch);
            }
        });
    }

    @ClientOnly
    public static void onServerAnimate(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final BlockPos pos = buf.readBlockPos();
        final String animation = buf.readUtf();
        client.execute(() -> {
            assert client.level != null;
            final BlockEntity blockEntity = client.level.getBlockEntity(pos);
            AnimationState state = null;
            if (blockEntity instanceof ServerAnimatable serverAnimatable) {
                state = serverAnimatable.getAnimation(animation);
            }
            if (state != null) {
                // state can only ever be non-null if blockEntity instanceof ServerAnimatable
                state.start(((ServerAnimatable)blockEntity).getAge());
            } else {
                PortalCubed.LOGGER.warn("Unknown animation from {}: {} for {}", SERVER_ANIMATE, animation, blockEntity);
            }
        });
    }

    @ClientOnly
    public static void onSetCustomFog(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        PortalCubedClient.customFog = FogSettings.decodeOptional(buf);
    }

    @ClientOnly
    public static void onSetCameraInterpolate(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        PortalCubedClient.cameraInterpStart = new IPQuaternion(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        PortalCubedClient.cameraInterpStartTime = System.currentTimeMillis();
    }
}
