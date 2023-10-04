package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.render.portal.PortalRenderTarget;
import com.fusionflux.portalcubed.client.render.portal.PortalRendering;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.*;

// Based on https://github.com/iPortalTeam/ImmersivePortalsMod/blob/55c9c1e7e298e09d8d43b0114e64e30271aa43b6/imm_ptl_core/src/main/java/qouteall/imm_ptl/core/mixin/client/render/framebuffer/MixinRenderTarget.java#L3
@Mixin(RenderTarget.class)
public abstract class RenderTargetMixin {
	@ModifyArgs(
		method = "createBuffers",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
			remap = false
		)
	)
	private void texImage2D(Args args) {
		if ((Object) this instanceof PortalRenderTarget && args.get(2).equals(GL_DEPTH_COMPONENT)) {
			args.set(2, GL_DEPTH24_STENCIL8);
			args.set(6, ARBFramebufferObject.GL_DEPTH_STENCIL);
			args.set(7, GL_UNSIGNED_INT_24_8);
		}
	}

	@ModifyArgs(
		method = "createBuffers",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V",
			remap = false
		)
	)
	private void framebufferTexture2D(Args args) {
		if ((Object) this instanceof PortalRenderTarget && args.get(1).equals(GL_DEPTH_ATTACHMENT)) {
			args.set(1, GL_DEPTH_STENCIL_ATTACHMENT);
		}
	}

	@Inject(method = "bindWrite", at = @At("HEAD"), cancellable = true)
	private void portalcubed$cancelBindWriteIfPortalRendering(boolean setViewport, CallbackInfo ci) {
		if (PortalRendering.isRendering() && !((Object) this instanceof PortalRenderTarget)) ci.cancel();
	}
}
