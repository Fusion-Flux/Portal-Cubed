package com.fusionflux.portalcubed.accessor;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.jetbrains.annotations.Nullable;

public interface BlockElementExt {
	@Nullable
	String portalcubed$getName();

	void portalcubed$setName(String name);

	@Nullable
	RenderMaterial portalcubed$getRenderMaterial();

	void portalcubed$setRenderMaterial(RenderMaterial material);
}
