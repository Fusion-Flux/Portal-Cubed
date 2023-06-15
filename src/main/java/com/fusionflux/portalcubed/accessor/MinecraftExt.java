package com.fusionflux.portalcubed.accessor;

import net.minecraft.client.renderer.RenderBuffers;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface MinecraftExt {
    void setRenderBuffers(RenderBuffers buffers);
}
