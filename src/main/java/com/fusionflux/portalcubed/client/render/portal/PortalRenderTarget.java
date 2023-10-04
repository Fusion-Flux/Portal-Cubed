package com.fusionflux.portalcubed.client.render.portal;

import com.mojang.blaze3d.pipeline.TextureTarget;

import net.minecraft.client.Minecraft;

/**
 * Stencil code is in {@link com.fusionflux.portalcubed.mixin.client.RenderTargetMixin}
*/
public final class PortalRenderTarget extends TextureTarget {
	PortalRenderTarget(int width, int height) {
		super(width, height, true, Minecraft.ON_OSX);
	}
}
