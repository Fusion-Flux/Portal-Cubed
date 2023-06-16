package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.accessor.GameRendererExt;
import com.fusionflux.portalcubed.accessor.LevelRendererExt;
import com.fusionflux.portalcubed.accessor.MinecraftExt;
import com.fusionflux.portalcubed.client.render.entity.PortalRenderer;
import com.fusionflux.portalcubed.entity.Portal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class PortalRendererImpl {
    private static final Deque<RenderBuffers> RENDER_BUFFERS_POOL = new ArrayDeque<>(4);
    private static int buffersPoolCount = 0;

    protected static final int MAX_PORTAL_LAYER = 1; // No recursive rendering yet

    public abstract boolean enabled(Portal portal);

    public abstract void preRender(Portal portal, float tickDelta, PoseStack poseStack);

    public abstract void postRender(Portal portal, float tickDelta, PoseStack poseStack);

    public abstract PortalRenderPhase targetPhase();

    protected void renderWorld(Portal portal, float tickDelta) {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.getProfiler().push("pc_portal_render");

        final Camera oldCamera = minecraft.gameRenderer.getMainCamera();
        final Frustum oldFrustum = ((LevelRendererExt)minecraft.levelRenderer).getCullingFrustum();
        final RenderBuffers oldRenderBuffers = ((LevelRendererExt)minecraft.levelRenderer).getRenderBuffers();
        final RenderBuffers oldGlobalBuffers = minecraft.renderBuffers();

        final Camera newCamera = new Camera();
        ((CameraExt)newCamera).updateSimple(portal.level, portal);

        final RenderBuffers newRenderBuffers = newRenderBuffers();
        if (newRenderBuffers != null) {
            ((LevelRendererExt)minecraft.levelRenderer).setRenderBuffers(newRenderBuffers);
            ((MinecraftExt)minecraft).setRenderBuffers(newRenderBuffers);
        }

        ((GameRendererExt)minecraft.gameRenderer).setMainCamera(newCamera);

        final PortalRenderPhase phase = PortalRenderer.renderPhase;
        PortalRenderer.renderPhase = PortalRenderPhase.ENTITY;
        minecraft.gameRenderer.renderLevel(tickDelta, Util.getNanos(), new PoseStack());
        PortalRenderer.renderPhase = phase;

        ((GameRendererExt)minecraft.gameRenderer).setMainCamera(oldCamera);
        ((LevelRendererExt)minecraft.levelRenderer).setCullingFrustum(oldFrustum);
        ((LevelRendererExt)minecraft.levelRenderer).setRenderBuffers(oldRenderBuffers);
        ((MinecraftExt)minecraft).setRenderBuffers(oldGlobalBuffers);

        if (newRenderBuffers != null) {
            freeRenderBuffers(newRenderBuffers);
        }

        minecraft.getProfiler().pop();
    }

    private static RenderBuffers newRenderBuffers() {
        if (buffersPoolCount >= 2) {
            return null;
        }
        buffersPoolCount--;
        if (RENDER_BUFFERS_POOL.isEmpty()) {
            return new RenderBuffers();
        }
        return RENDER_BUFFERS_POOL.pop();
    }

    private static void freeRenderBuffers(RenderBuffers buffers) {
        buffersPoolCount++;
        RENDER_BUFFERS_POOL.add(buffers);
    }
}
