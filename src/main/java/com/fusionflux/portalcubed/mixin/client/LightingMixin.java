package com.fusionflux.portalcubed.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.fusionflux.portalcubed.client.render.portal.PortalRendering;
import com.mojang.blaze3d.platform.Lighting;

@Mixin(Lighting.class)
public abstract class LightingMixin {
	@ModifyVariable(method = {"setupLevel", "setupNetherLevel"}, at = @At("HEAD"), index = 0, argsOnly = true)
	private static Matrix4f portalcubed$fixLevelLighting(Matrix4f mat) {
		if (PortalRendering.isRendering()) mat = new Matrix4f(mat).setTranslation(0, 0, 0);
		return mat;
	}
}
