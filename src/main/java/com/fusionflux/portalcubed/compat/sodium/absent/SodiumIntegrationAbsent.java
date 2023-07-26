package com.fusionflux.portalcubed.compat.sodium.absent;

import com.fusionflux.portalcubed.compat.sodium.SodiumIntegration;
import net.minecraft.client.renderer.texture.SpriteContents;

public enum SodiumIntegrationAbsent implements SodiumIntegration {
    INSTANCE;

    @Override
    public void markSpriteActive(SpriteContents sprite) {
    }
}
