package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public abstract class PortalRenderer {
    protected static final int MAX_PORTAL_LAYER = 1; // No recursive rendering yet

    public abstract boolean enabled(ExperimentalPortal portal);

    public abstract void preRender(ExperimentalPortal portal, float tickDelta, PoseStack poseStack);

    public abstract void postRender(ExperimentalPortal portal, float tickDelta, PoseStack poseStack);

    protected void renderWorld(ExperimentalPortal portal, float tickDelta, PoseStack poseStack) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Camera camera = new Camera();
        ((CameraExt)camera).updateSimple(portal.level, portal);
        minecraft.levelRenderer.renderLevel(
            new PoseStack(),
            tickDelta,
            0,
            false,
            camera,
            minecraft.gameRenderer,
            minecraft.gameRenderer.lightTexture(),
            poseStack.last().pose()
        );
    }
}
