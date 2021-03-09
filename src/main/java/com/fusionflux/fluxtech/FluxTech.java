package com.fusionflux.fluxtech;

//import com.fusionflux.fluxtech.config.FluxTechConfig;

import com.fusionflux.fluxtech.blocks.FluxTechBlocks;
import com.fusionflux.fluxtech.config.FluxTechConfig2;
import com.fusionflux.fluxtech.entity.FluxTechEntities;
import com.fusionflux.fluxtech.items.FluxTechItems;
import com.fusionflux.fluxtech.items.PortalGun;
import com.fusionflux.fluxtech.sound.FluxTechSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

public class FluxTech implements ModInitializer {

    public static final FluxTechConfig2 CONFIG = new FluxTechConfig2();

    public static final String MOD_ID = "fluxtech";

    public static final ItemGroup FLUXTECH_GROUP = FabricItemGroupBuilder.build(
            new Identifier("fluxtech", "general"),
            () -> new ItemStack(FluxTechBlocks.BOTTOM_2X2_GRITTY_WHITE_PANEL));

    @Override
    public void onInitialize() {
        FluxTechConfig2.register();
        FluxTechBlocks.registerBlocks();
        FluxTechItems.registerItems();
        FluxTechEntities.registerEntities();
        FluxTechSounds.registerSounds();
        registerPacketListener();
    }

    private void registerPacketListener() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID, "portal_left_click"), (server, player, handler, buf, responseSender) -> {
            ServerWorld serverWorld = player.getServerWorld();
            Hand hand = buf.readEnumConstant(Hand.class);
            ItemStack itemStack = player.getStackInHand(hand);
            player.updateLastActionTime();
            if (!itemStack.isEmpty() && itemStack.getItem() == FluxTechItems.PORTAL_GUN) {
                server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));
            }
        });
    }

}
