package com.fusionflux.portalcubed.accessor;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.jetbrains.annotations.Nullable;

public interface BakedQuadExt {
	@Nullable
	RenderMaterial portalcubed$getRenderMaterial();

	void portalcubed$setRenderMaterial(RenderMaterial material);
}
