package com.fusionflux.portalcubed.client.render.portal;

import java.util.function.Supplier;

public enum PortalRenderers {
    DISABLED(DisabledRenderer::new),
    STENCIL(StencilRenderer::new),
    FRAMEBUFFER(FramebufferRenderer::new);

    public final Supplier<? extends PortalRendererImpl> creator;

    PortalRenderers(Supplier<? extends PortalRendererImpl> creator) {
        this.creator = creator;
    }
}
