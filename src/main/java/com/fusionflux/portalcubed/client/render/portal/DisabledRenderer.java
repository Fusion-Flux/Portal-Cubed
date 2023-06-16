package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.entity.Portal;
import com.mojang.blaze3d.vertex.PoseStack;

public class DisabledRenderer extends PortalRendererImpl {
    @Override
    public boolean enabled(Portal portal) {
        return false;
    }

    @Override
    public void preRender(Portal portal, float tickDelta, PoseStack poseStack) {
    }

    @Override
    public void postRender(Portal portal, float tickDelta, PoseStack poseStack) {
    }

    @Override
    public PortalRenderPhase targetPhase() {
        return PortalRenderPhase.ENTITY;
    }
}
