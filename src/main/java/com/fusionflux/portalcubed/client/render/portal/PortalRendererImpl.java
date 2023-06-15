package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.entity.Portal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public abstract class PortalRendererImpl {
    protected static final int MAX_PORTAL_LAYER = 1; // No recursive rendering yet

    public abstract boolean enabled(Portal portal);

    public abstract void preRender(Portal portal, float tickDelta, PoseStack poseStack);

    public abstract void postRender(Portal portal, float tickDelta, PoseStack poseStack);

    protected void renderWorld(Portal portal, float tickDelta) {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.getProfiler().push("pc_portal_render");
        final Camera camera = new Camera();
        ((CameraExt)camera).updateSimple(portal.level, portal);
        minecraft.gameRenderer.renderLevel(tickDelta, Util.getNanos(), new PoseStack());
        minecraft.getProfiler().pop();
    }
}
