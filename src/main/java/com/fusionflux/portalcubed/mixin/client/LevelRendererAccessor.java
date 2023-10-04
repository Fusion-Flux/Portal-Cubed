package com.fusionflux.portalcubed.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Mutable
    @Accessor("renderChunksInFrustum")
    void portalcubed$setRenderChunksInFrustum(ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum);
    @Accessor("renderChunksInFrustum")
    ObjectArrayList<LevelRenderer.RenderChunkInfo> portalcubed$getRenderChunksInFrustum();

    @Accessor("cullingFrustum")
    Frustum portalcubed$getCullingFrustum();
    @Accessor("cullingFrustum")
    void portalcubed$setCullingFrustum(Frustum frustum);

    @Accessor("capturedFrustum")
    void portalcubed$setCapturedFrustum(Frustum frustum);
}
