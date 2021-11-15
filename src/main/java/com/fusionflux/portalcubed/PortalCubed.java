package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.accessor.QuaternionHandler;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class PortalCubed implements ModInitializer {
    public static final PortalCubedConfig CONFIG = new PortalCubedConfig();

    public static final String MODID = "portalcubed";

    public static final ItemGroup PortalCubedGroup = FabricItemGroupBuilder.build(
            id("general"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }



    @Override
    public void onInitialize() {

        QuaternionHandler.QUATERNION_HANDLER.getClass();
        PortalCubedConfig.register();
        PortalCubedBlocks.registerBlocks();
        PortalCubedItems.registerItems();
        PortalCubedEntities.registerEntities();
        PortalCubedServerPackets.registerPackets();
        PortalCubedSounds.registerSounds();
        FlammableBlockRegistry.getDefaultInstance().add(PortalCubedBlocks.NEUROTOXIN_BLOCK,10000,10000);
    }
}
