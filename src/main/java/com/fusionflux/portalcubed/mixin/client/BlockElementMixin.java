package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.block.model.BlockElement;

@Mixin(BlockElement.class)
public class BlockElementMixin implements BlockElementExt {
	@Unique
	private String name;
	@Unique
	private RenderMaterial material;

	@Override
	@Nullable
	public String portalcubed$getName() {
		return this.name;
	}

	@Override
	public void portalcubed$setName(String name) {
		this.name = name;
	}

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
