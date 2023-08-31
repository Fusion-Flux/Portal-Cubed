package com.fusionflux.portalcubed.compat.sodium;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.compat.sodium.absent.SodiumIntegrationAbsent;
import com.fusionflux.portalcubed.compat.sodium.present.SodiumIntegrationPresent;

import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.quiltmc.loader.api.QuiltLoader;

public interface SodiumIntegration {
    SodiumIntegration INSTANCE = Util.make(() -> {
        if (!QuiltLoader.isModLoaded("sodium")) {
            return SodiumIntegrationAbsent.INSTANCE;
        }

       try {
           Class<?> spriteContentsEx = Class.forName("me.jellysquid.mods.sodium.client.render.texture.SpriteContentsExtended");
           spriteContentsEx.getDeclaredMethod("sodium$setActive", Boolean.TYPE);
           return SodiumPresentHolder.create();
       } catch (ClassNotFoundException | NoSuchMethodException e) {
           PortalCubed.LOGGER.error("Portal Cubed has outdated Sodium compatibility! Some things may not work properly.");
           return SodiumIntegrationAbsent.INSTANCE;
       }
    });

    void markSpriteActive(SpriteContents sprite);

    class SodiumPresentHolder {
        private static SodiumIntegration create() {
            return new SodiumIntegrationPresent();
        }
    }
}
