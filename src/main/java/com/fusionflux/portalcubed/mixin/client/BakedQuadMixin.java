package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BakedQuadExt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.block.model.BakedQuad;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadExt {
	@Unique
	private String portalcubed$renderType;

	@Override
	public @Nullable String portalcubed$getRenderType() {
		return portalcubed$renderType;
	}

	@Override
	public void portalcubed$setRenderType(String type) {
		portalcubed$renderType = type;
	}
}
