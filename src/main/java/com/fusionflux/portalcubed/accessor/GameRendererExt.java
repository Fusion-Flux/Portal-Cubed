package com.fusionflux.portalcubed.accessor;

import net.minecraft.client.Camera;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface GameRendererExt {
	void setMainCamera(Camera camera);
}
