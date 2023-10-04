package com.fusionflux.portalcubed.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(RenderSystem.class)
public interface RenderSystemAccessor {
	@Mutable
	@Accessor
	static void setModelViewStack(PoseStack poseStack) {
		throw new AssertionError();
	}
}
