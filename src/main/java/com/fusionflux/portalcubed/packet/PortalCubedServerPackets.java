package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalCubedComponents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

public class PortalCubedServerPackets {
    public static final Identifier GRAB_KEY_PRESSED = new Identifier(PortalCubed.MOD_ID, "grab_key_pressed");
    public static final Identifier REMOVE_PORTALS = new Identifier(PortalCubed.MOD_ID, "remove_portals");

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, @SuppressWarnings("unused") PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {

        Vec3d vec3d = player.getCameraPosVec(0);
        double d = 5;

        Vec3d vec3d2 = player.getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);

        server.execute(() -> {
            EntityHitResult entityHitResult = ProjectileUtil.raycast(player, vec3d, vec3d3, box, (entity) -> !entity.isSpectator() && entity.collides(), d);
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof CorePhysicsEntity entity) {
                    if (!PortalCubedComponents.HOLDER_COMPONENT.get(player).hold(entity)) {
                        PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding();
                    }
                }
            } else {
                if (!PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding()) {
                    player.playSound(PortalCubedSounds.NOTHING_TO_GRAB_EVENT, SoundCategory.NEUTRAL, 0.3f, 1f);
                    ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                }
            }
        });
    }

    public static void onRemovePortalKeyPressed(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, @SuppressWarnings("unused") PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        server.execute(() -> {
            boolean foundPortal = false;
            for (final UUID portal : List.copyOf(CalledValues.getPortals(player))) {
                final Entity checkPortal = ((ServerWorld)player.world).getEntity(portal);
                if (checkPortal != null) {
                    foundPortal = true;
                    checkPortal.kill();
                }
            }
            if (foundPortal) {
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                player.playSound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundCategory.NEUTRAL, 0.5f, 1f);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
            }
        });
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, PortalCubedServerPackets::onGrabKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_PORTALS, PortalCubedServerPackets::onRemovePortalKeyPressed);
    }
}
