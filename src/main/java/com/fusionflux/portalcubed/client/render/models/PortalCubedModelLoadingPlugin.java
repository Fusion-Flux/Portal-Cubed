package com.fusionflux.portalcubed.client.render.models;

import com.fusionflux.portalcubed.client.render.models.emissive.EmissiveData;
import com.fusionflux.portalcubed.client.render.models.emissive.EmissiveWrapper;
import com.fusionflux.portalcubed.client.render.models.rendertype.MultiRenderTypeWrapper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public enum PortalCubedModelLoadingPlugin implements PreparableModelLoadingPlugin<EmissiveData> {
	INSTANCE;

	@Override
	public void onInitializeModelLoader(EmissiveData emissiveData, Context ctx) {
		ctx.modifyModelBeforeBake().register(ModelModifier.WRAP_PHASE, new MultiRenderTypeWrapper());
		ctx.modifyModelAfterBake().register(ModelModifier.WRAP_PHASE, new EmissiveWrapper(emissiveData));
	}
}
