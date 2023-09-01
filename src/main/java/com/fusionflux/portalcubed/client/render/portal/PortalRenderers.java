package com.fusionflux.portalcubed.client.render.portal;

import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef") // Using a lambda makes the classes be loaded more lazily, preventing crashes on the dedicated server
public enum PortalRenderers {
	DISABLED(() -> new DisabledRenderer()),
	STENCIL(() -> new StencilRenderer()),
	FRAMEBUFFER(() -> new FramebufferRenderer());

	public final Supplier<? extends PortalRendererImpl> creator;

	PortalRenderers(Supplier<? extends PortalRendererImpl> creator) {
		this.creator = creator;
	}
}
