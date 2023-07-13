package com.fusionflux.portalcubed.compat.sodium.present;

import com.fusionflux.portalcubed.compat.sodium.SodiumIntegration;
import me.jellysquid.mods.sodium.client.render.texture.SpriteExtended;
import net.minecraft.client.renderer.texture.SpriteContents;

public class SodiumIntegrationPresent implements SodiumIntegration {
    @Override
    public void markSpriteActive(SpriteContents sprite) {
        if (sprite instanceof SpriteExtended extended) {
            extended.setActive(true);
        }
    }
}
