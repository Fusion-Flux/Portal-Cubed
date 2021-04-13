package com.fusionflux.thinkingwithportatos.packet;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ThinkingWithPortatosServerPackets {
    public static final Identifier PORTAL_LEFT_CLICK = new Identifier(ThinkingWithPortatos.MODID, "portal_left_click");
    public static final Identifier GRAB_KEY_PRESSED = new Identifier(ThinkingWithPortatos.MODID, "grab_key_pressed");

    public static void onPortalLeftClick(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ServerWorld serverWorld = player.getServerWorld();
        Hand hand = buf.readEnumConstant(Hand.class);
        ItemStack itemStack = player.getStackInHand(hand);
        player.updateLastActionTime();

        if (!itemStack.isEmpty() && itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN || itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2) {
            server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }
    }

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.readInt();

        server.execute(() -> {
            if (entityId == -1) {
                if (ThinkingWithPortatos.getBodyGrabbingManager(false).isPlayerGrabbing(player)) {
                    ThinkingWithPortatos.getBodyGrabbingManager(false).tryUngrab(player);
                }
            } else {
                Entity entity = player.world.getEntityById(entityId);

                if (entity != null) {
                    boolean isGrabbed = ThinkingWithPortatos.getBodyGrabbingManager(player.world.isClient).isGrabbed(entity);

                    if (player.getMainHandStack().getItem() instanceof PortalGun && !isGrabbed) {
                        ThinkingWithPortatos.getBodyGrabbingManager(false).tryGrab(player, entity);
                    }
                }
            }
        });
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PORTAL_LEFT_CLICK, ThinkingWithPortatosServerPackets::onPortalLeftClick);
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, ThinkingWithPortatosServerPackets::onGrabKeyPressed);
    }
}
