package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.items.PaintGun;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.items.PortalGunPrimary;
import com.fusionflux.portalcubed.items.PortalGunSecondary;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
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
    public static final Identifier PORTAL_LEFT_CLICK = new Identifier(PortalCubed.MOD_ID, "portal_left_click");
    public static final Identifier GRAB_KEY_PRESSED = new Identifier(PortalCubed.MOD_ID, "grab_key_pressed");
    public static final Identifier REMOVE_PORTALS = new Identifier(PortalCubed.MOD_ID, "remove_portals");

    public static void onPortalLeftClick(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        ServerWorld serverWorld = player.getWorld();
        Hand hand = buf.readEnumConstant(Hand.class);
        ItemStack itemStack = player.getStackInHand(hand);
        player.updateLastActionTime();

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PortalGun) {
            server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PortalGunPrimary) {
            server.execute(() -> ((PortalGunPrimary) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PortalGunSecondary) {
            server.execute(() -> ((PortalGunSecondary) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PaintGun) {
            server.execute(() -> ((PaintGun) itemStack.getItem()).useLeft(serverWorld, player));
        }
    }

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
                    if (CalledValues.getCubeUUID(player)==null) {
                        entity.setHolderUUID(player.getUuid());
                        CalledValues.setCubeUUID(player,entity.getUuid());
                    } else {
                        CorePhysicsEntity playerCube = (CorePhysicsEntity) ((ServerWorld) player.world).getEntity(CalledValues.getCubeUUID(player));
                        if (playerCube != null) {
                            playerCube.setHolderUUID(null);
                        }
                        CalledValues.setCubeUUID(player,null);
                    }
                }
            } else {
                CorePhysicsEntity playerCube = (CorePhysicsEntity) ((ServerWorld) player.world).getEntity(CalledValues.getCubeUUID(player));
                if (playerCube != null) {
                    playerCube.setHolderUUID(null);
                } else {
                    player.playSound(PortalCubedSounds.NOTHING_TO_GRAB_EVENT, SoundCategory.NEUTRAL, 0.3f, 1f);
                    ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                }
                CalledValues.setCubeUUID(player,null);
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
        ServerPlayNetworking.registerGlobalReceiver(PORTAL_LEFT_CLICK, PortalCubedServerPackets::onPortalLeftClick);
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, PortalCubedServerPackets::onGrabKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_PORTALS, PortalCubedServerPackets::onRemovePortalKeyPressed);
    }
}
