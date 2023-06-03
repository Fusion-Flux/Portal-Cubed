package com.fusionflux.portalcubed.client.render.portal;

import java.util.function.Supplier;

public enum PortalRenderers {
    DISABLED(DisabledRenderer::new),
    STENCIL(StencilRenderer::new),
    FRAMEBUFFER(FramebufferRenderer::new);

    public final Supplier<? extends PortalRenderer> creator;

    PortalRenderers(Supplier<? extends PortalRenderer> creator) {
        this.creator = creator;
    }
}
