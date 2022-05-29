package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.accessor.QuaternionHandler;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.MidnightConfig;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class PortalCubed implements ModInitializer {
    public static final PortalCubedConfig CONFIG = new PortalCubedConfig();

    public static final String MODID = "portalcubed";

    public static final ItemGroup PortalCubedGroup = QuiltItemGroup.createWithIcon(
            id("general"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize(ModContainer mod) {
        ServerPlayNetworking.registerGlobalReceiver(id("portalpacket"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the bytebuf
            double x =  buf.readDouble();
            double y =  buf.readDouble();
            double z =  buf.readDouble();
            boolean tp =  buf.readBoolean();
            server.execute(() -> {
                ((EntityAttachments)(player)).setServerVel(new Vec3d(x,y,z));
                ((EntityAttachments)(player)).setShouldTeleport(tp);
            });
        });
        QuaternionHandler.QUATERNION_HANDLER.getClass();
        MidnightConfig.init("portalcubed", PortalCubedConfig.class);
        PortalCubedBlocks.registerBlocks();
        PortalCubedItems.registerItems();
        PortalCubedEntities.registerEntities();
        PortalCubedServerPackets.registerPackets();
        PortalCubedSounds.registerSounds();
        FlammableBlockRegistry.getDefaultInstance().add(PortalCubedBlocks.NEUROTOXIN_BLOCK,10000,10000);
    }
}
