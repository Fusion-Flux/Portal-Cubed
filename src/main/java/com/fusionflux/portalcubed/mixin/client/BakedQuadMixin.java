package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BakedQuadExt;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.block.model.BakedQuad;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadExt {
	@Unique
	private RenderMaterial material;

	@Override
	@Nullable
	public RenderMaterial portalcubed$getRenderMaterial() {
		return this.material;
	}

	@Override
	public void portalcubed$setRenderMaterial(RenderMaterial material) {
		this.material = material;
	}
}
