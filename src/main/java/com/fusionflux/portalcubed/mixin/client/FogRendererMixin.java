package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.fluids.ToxicGooFluid;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FluidState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Shadow
	private static float fogRed;
	@Shadow
	private static float fogGreen;
	@Shadow
	private static float fogBlue;
	@Shadow
	private static long biomeChangedTime;

	@Inject(method = "setupColor", at = @At("HEAD"), cancellable = true)
	private static void renderToxicGooFog(Camera camera, float tickDelta, ClientLevel world, int viewDistance, float skyDarkness, CallbackInfo ci) {
		FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

		if (cameraSubmergedFluidState.getType() instanceof ToxicGooFluid) {
			ci.cancel();

			// Dark brownish "red"
			fogRed = 99 / 255f;
			fogGreen = 29 / 255f;
			fogBlue = 1 / 255f;

			biomeChangedTime = -1L;

			// Dark brownish "red"
			RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0f);
		}
	}

	@Inject(
		method = "setupColor",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;pow(DD)D"
		),
		cancellable = true
	)
	private static void renderPortalFog(Camera camera, float tickDelta, ClientLevel world, int viewDistance, float skyDarkness, CallbackInfo ci) {
		final FogSettings fog = PortalCubedClient.customFog;
		if (fog != null) {
			ci.cancel();

			fogRed = fog.color().r() / 255f;
			fogGreen = fog.color().g() / 255f;
			fogBlue = fog.color().b() / 255f;

			RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0f);
		}
	}

	@Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
	private static void applyToxicGooFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

		if (cameraSubmergedFluidState.getType() instanceof ToxicGooFluid) {
			ci.cancel();

			if (camera.getEntity().isSpectator()) {
				RenderSystem.setShaderFogStart(-8.0F);
				RenderSystem.setShaderFogEnd(viewDistance * 0.5F);
			} else {
				// Same fog as lava with fire resistance
				RenderSystem.setShaderFogStart(0.0F);
				RenderSystem.setShaderFogEnd(3.0F);
			}
			RenderSystem.setShaderFogShape(FogShape.SPHERE);
		}
	}

	@Inject(
		method = "setupFog",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/FogRenderer$FogMode;FOG_SKY:Lnet/minecraft/client/renderer/FogRenderer$FogMode;",
			opcode = Opcodes.GETSTATIC
		),
		cancellable = true
	)
	private static void applyPortalFog1(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		applyPortalFog0(ci);
	}

	@Inject(
		method = "setupFog",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;min(FF)F"
		),
		cancellable = true
	)
	private static void applyPortalFog2(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		applyPortalFog0(ci);
	}

	private static void applyPortalFog0(CallbackInfo ci) {
		final FogSettings fog = PortalCubedClient.customFog;
		if (fog != null) {
			ci.cancel();

			RenderSystem.setShaderFogStart(fog.start());
			RenderSystem.setShaderFogEnd(fog.end());
			RenderSystem.setShaderFogShape(fog.shape().toBlaze3d());
		}
	}
}
