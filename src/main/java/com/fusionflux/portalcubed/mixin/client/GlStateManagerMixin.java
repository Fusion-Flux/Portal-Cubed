package com.fusionflux.portalcubed.mixin.client;

import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fusionflux.portalcubed.client.render.portal.PortalRendering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(GlStateManager.class)
public abstract class GlStateManagerMixin {
	private static Vector3i portalcubed$capturedClearColor = new Vector3i();

	@Inject(method = "_clearColor", at = @At("HEAD"), cancellable = true, remap = false)
	private static void portalcubed$captureClearColor(float r, float g, float b, float a, CallbackInfo ci) {
		if (PortalRendering.isRendering()) {
			RenderSystem.assertOnRenderThreadOrInit();
			portalcubed$capturedClearColor.set((int) (r * 255), (int) (g * 255), (int) (b * 255));
			ci.cancel();
		}
	}

	@Inject(method = "_clear", at = @At("HEAD"), cancellable = true, remap = false)
	private static void portalcubed$replaceClearingIfRenderingPortal(int mask, boolean checkError, CallbackInfo ci) {
		if (PortalRendering.isRendering()) {
			RenderSystem.assertOnRenderThreadOrInit();
			GlStateManager._depthMask(false);
			PortalRendering.renderScreenTriangle(portalcubed$capturedClearColor.x, portalcubed$capturedClearColor.y, portalcubed$capturedClearColor.z);
			GlStateManager._depthMask(true);
			ci.cancel();
		}
	}
}
