package com.fusionflux.portalcubed.compat.sodium;

import com.fusionflux.portalcubed.compat.sodium.absent.SodiumIntegrationAbsent;
import com.fusionflux.portalcubed.compat.sodium.present.SodiumIntegrationPresent;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.quiltmc.loader.api.QuiltLoader;

public interface SodiumIntegration {
    SodiumIntegration INSTANCE = QuiltLoader.isModLoaded("sodium") ? SodiumPresentHolder.create() : SodiumIntegrationAbsent.INSTANCE;

    void markSpriteActive(SpriteContents sprite);

    class SodiumPresentHolder {
        private static SodiumIntegration create() {
            return new SodiumIntegrationPresent();
        }
    }
}
