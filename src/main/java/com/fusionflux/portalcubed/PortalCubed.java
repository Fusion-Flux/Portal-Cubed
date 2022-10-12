package com.fusionflux.portalcubed;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.QuaternionHandler;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
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
import net.minecraft.util.math.Direction;
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
            float yawSet =  buf.readFloat();
            server.execute(() -> {
                player.setYaw(yawSet);
                player.requestTeleport(x,y,z);
                CalledValues.setHasTeleportationHappened(player,true);
                GravityChangerAPI.clearGravity(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("clientteleportupdate"), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                CalledValues.setHasTeleportationHappened(player,false);
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
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);
    }
}
