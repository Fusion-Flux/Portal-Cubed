package com.fusionflux.portalcubed.client.render.entity.animatedtextures;

import com.fusionflux.portalcubed.compat.sodium.SodiumIntegration;
import com.fusionflux.portalcubed.mixin.client.SpriteContentsAccessor;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class AnimatedTexture extends AbstractTexture implements Tickable {
    public final ResourceLocation texture;

    private SpriteContents contents;
    private SpriteTicker ticker;

    public AnimatedTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        Resource resource = manager.getResourceOrThrow(texture);
        this.contents = SpriteLoader.loadSprite(texture, resource);
        if (contents instanceof SpriteContentsAccessor access) { // != null
            NativeImage image = access.getOriginalImage();
            if (RenderSystem.isOnRenderThreadOrInit()) {
                init(image);
            } else {
                RenderSystem.recordRenderCall(() -> init(image));
            }
            this.ticker = contents.createTicker();
        }
    }

    private void init(NativeImage image) {
        TextureUtil.prepareImage(this.getId(), 0, image.getWidth(), image.getHeight());
        contents.uploadFirstFrame(0, 0);
    }

    @Override
    public void tick() {
        if (ticker != null) {
            if (RenderSystem.isOnRenderThread()) {
                cycle();
            } else {
                RenderSystem.recordRenderCall(this::cycle);
            }
        }
    }

    private void cycle() {
        SodiumIntegration.INSTANCE.markSpriteActive(contents);
        bind();
        ticker.tickAndUpload(0, 0);
    }
}
