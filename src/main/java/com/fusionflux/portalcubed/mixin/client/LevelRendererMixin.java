package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.portal.PortalRendering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow @Final private Minecraft minecraft;

	@ModifyVariable(method = "setupRender", at = @At("HEAD"), index = 3, argsOnly = true)
	private boolean portalcubed$denyRefreshIfPortalRendering(boolean org) {
		return PortalRendering.isRendering() ? true : org;
	}

	@Inject(method = "setupRender", at = @At("HEAD"), cancellable = true)
	private void portalcubed$cancelSetupRenderIfPortalRendering(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
		if (PortalRendering.isRendering()) ci.cancel();
	}

	@Inject(method = "prepareCullFrustum", at = @At("HEAD"))
	private void modifyCameraRotation(PoseStack poseStack, Vec3 cameraPos, Matrix4f projectionMatrix, CallbackInfo ci) {
		PortalCubedClient.interpCamera().ifPresent(q -> poseStack.mulPose(q.toQuaternionf()));
		if (PortalCubedClient.cameraTransformedThroughPortal != null && !minecraft.gameRenderer.getMainCamera().isDetached()) {
			poseStack.mulPose(PortalCubedClient.cameraTransformedThroughPortal.getTransformQuat().toQuaternionf().conjugate());
		}
	}

	@WrapOperation(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;isDetached()Z"
		)
	)
	private boolean overrideDetached(Camera instance, Operation<Boolean> original) {
		return PortalCubedClient.cameraTransformedThroughPortal != null || original.call(instance);
	}
}
