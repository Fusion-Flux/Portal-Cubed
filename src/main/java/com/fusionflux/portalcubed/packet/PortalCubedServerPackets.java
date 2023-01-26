package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
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

public class PortalCubedServerPackets {
    public static final Identifier PORTAL_LEFT_CLICK = new Identifier(PortalCubed.MODID, "portal_left_click");
    public static final Identifier GRAB_KEY_PRESSED = new Identifier(PortalCubed.MODID, "grab_key_pressed");

    public static void onPortalLeftClick(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ServerWorld serverWorld = player.getWorld();
        Hand hand = buf.readEnumConstant(Hand.class);
        ItemStack itemStack = player.getStackInHand(hand);
        player.updateLastActionTime();

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PortalGun) {
            server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }
    }

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {

        Vec3d vec3d = player.getCameraPosVec(0);
        double d = 5;

        Vec3d vec3d2 = player.getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f = 1.0F;
        Box box = player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);

        server.execute(() -> {
            EntityHitResult entityHitResult = ProjectileUtil.raycast(player, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides(), d);
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof CorePhysicsEntity entity) {
                    if (CalledValues.getCubeUUID(player)==null) {
                        entity.setHolderUUID(player.getUuid());
                        CalledValues.setCubeUUID(player,entity.getUuid());
                    } else {
                        CorePhysicsEntity playercube = (CorePhysicsEntity) ((ServerWorld) player.world).getEntity(CalledValues.getCubeUUID(player));
                        if (playercube != null) {
                            playercube.setHolderUUID(null);
                        }
                        CalledValues.setCubeUUID(player,null);
                    }
                } else {
                    entityHitResult.getEntity();
                }
            } else {
                CorePhysicsEntity playercube = (CorePhysicsEntity) ((ServerWorld) player.world).getEntity(CalledValues.getCubeUUID(player));
                if (playercube != null) {
                    playercube.setHolderUUID(null);
                } else {
                    player.playSound(PortalCubedSounds.NOTHING_TO_GRAB_EVENT, SoundCategory.NEUTRAL, 0.3f, 1f);
                    ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                }
                CalledValues.setCubeUUID(player,null);
            }
        });
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PORTAL_LEFT_CLICK, PortalCubedServerPackets::onPortalLeftClick);
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, PortalCubedServerPackets::onGrabKeyPressed);
    }
}
