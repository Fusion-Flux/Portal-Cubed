package com.fusionflux.portalcubed.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.texture.TextureAtlas;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
	@Invoker
	int invokeGetWidth();

	@Invoker
	int invokeGetHeight();
}
