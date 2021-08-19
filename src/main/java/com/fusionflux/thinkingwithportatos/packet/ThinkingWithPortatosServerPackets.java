package com.fusionflux.thinkingwithportatos.packet;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.physics.GrabUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
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

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof PortalGun) {
            server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
        }
    }

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        server.execute(() -> {
            Item mainHand = player.getMainHandStack().getItem();
            Item offHand = player.getOffHandStack().getItem();

            /*if (mainHand instanceof PortalGun || offHand instanceof PortalGun) {
                BodyGrabbingManager manager = ThinkingWithPortatos.getBodyGrabbingManager(false);

                if (manager.isPlayerGrabbing(player)) {
                    manager.tryUngrab(player, 0.0f);
                } else {
                    Entity entity = GrabUtil.getEntityToGrab(player);

                    if (entity == null) {
                        Entity block = GrabUtil.getBlockToGrab(player);

                        if (block != null) {
                            manager.tryGrab(player, block);
                        }
                    } else {
                        manager.tryGrab(player, entity);
                    }
                }
            }*/
        });
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PORTAL_LEFT_CLICK, ThinkingWithPortatosServerPackets::onPortalLeftClick);
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, ThinkingWithPortatosServerPackets::onGrabKeyPressed);
    }
}
