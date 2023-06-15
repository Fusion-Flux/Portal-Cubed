package com.fusionflux.portalcubed.accessor;

import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface LevelRendererExt {
    Frustum getCullingFrustum();

    void setCullingFrustum(Frustum frustum);

    RenderBuffers getRenderBuffers();

    void setRenderBuffers(RenderBuffers buffers);
}
