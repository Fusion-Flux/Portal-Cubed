package com.fusionflux.portalcubed.client.render.portal;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.mojang.blaze3d.vertex.PoseStack;

public class DisabledRenderer extends PortalRenderer {
    @Override
    public boolean enabled(ExperimentalPortal portal) {
        return false;
    }

    @Override
    public void preRender(ExperimentalPortal portal, float tickDelta, PoseStack poseStack) {
    }

    @Override
    public void postRender(ExperimentalPortal portal, float tickDelta, PoseStack poseStack) {
    }
}
