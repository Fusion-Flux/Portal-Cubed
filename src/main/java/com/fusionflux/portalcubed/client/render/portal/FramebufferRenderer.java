package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.mixin.client.MinecraftAccessor;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.mojang.blaze3d.platform.GlConst.GL_COLOR_BUFFER_BIT;
import static com.mojang.blaze3d.platform.GlConst.GL_DEPTH_BUFFER_BIT;

public class FramebufferRenderer extends PortalRendererImpl {
    private static final Queue<RenderTarget> FREE_TARGETS = new ArrayDeque<>();

    private int portalLayer = 0;

    private static RenderTarget getRenderTarget(int width, int height) {
        final RenderTarget target = FREE_TARGETS.poll();
        if (target == null) {
            return new TextureTarget(width, height, true, Minecraft.ON_OSX);
        }
        if (target.width != width || target.height != height) {
            target.resize(width, height, Minecraft.ON_OSX);
        }
        return target;
    }

    private static void freeRenderTarget(RenderTarget target) {
        if (!FREE_TARGETS.offer(target)) {
            target.destroyBuffers();
        }
    }

    @Override
    public boolean enabled(Portal portal) {
        return portalLayer < MAX_PORTAL_LAYER && portal.getActive();
    }

    @Override
    public void preRender(Portal portal, float tickDelta, PoseStack poseStack) {
    }

    @Override
    public void postRender(Portal portal, float tickDelta, PoseStack poseStack) {
        final Minecraft minecraft = Minecraft.getInstance();
        final RenderTarget oldTarget = minecraft.getMainRenderTarget();
        final RenderTarget newTarget = getRenderTarget(oldTarget.width, oldTarget.height);

        ((MinecraftAccessor)minecraft).setMainRenderTarget(newTarget);
        newTarget.bindWrite(true);

        GlStateManager._clearColor(1, 0, 1, 1);
        GlStateManager._clearDepth(1);
        GlStateManager._clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        portalLayer++;
        renderWorld(portal, tickDelta);
        portalLayer--;

        ((MinecraftAccessor)minecraft).setMainRenderTarget(oldTarget);
        oldTarget.bindWrite(true);

        freeRenderTarget(newTarget);
    }
}
