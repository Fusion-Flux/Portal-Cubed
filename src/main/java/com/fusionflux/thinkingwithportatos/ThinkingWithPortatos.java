package com.fusionflux.thinkingwithportatos;

//import com.fusionflux.fluxtech.config.FluxTechConfig;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ThinkingWithPortatos implements ModInitializer {

    public static final ThinkingWithPortatosConfig CONFIG = new ThinkingWithPortatosConfig();

    public static final String MOD_ID = "thinkingwithportatos";

    public static final ItemGroup ThinkingWithPortatosGroup = FabricItemGroupBuilder.build(
            new Identifier("thinkingwithportatos", "general"),
            () -> new ItemStack(ThinkingWithPortatosBlocks.BOTTOM_2X2_GRITTY_WHITE_PANEL));

    @Override
    public void onInitialize() {
        ThinkingWithPortatosConfig.register();
        ThinkingWithPortatosBlocks.registerBlocks();
        ThinkingWithPortatosItems.registerItems();
        ThinkingWithPortatosEntities.registerEntities();
        ThinkingWithPortatosSounds.registerSounds();
        registerPacketListener();
    }

    private void registerPacketListener() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID, "portal_left_click"), (server, player, handler, buf, responseSender) -> {
            ServerWorld serverWorld = player.getServerWorld();
            Hand hand = buf.readEnumConstant(Hand.class);
            ItemStack itemStack = player.getStackInHand(hand);
            player.updateLastActionTime();
            if (!itemStack.isEmpty() && itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN||itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2) {
                server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
            }
        });
    }

}
