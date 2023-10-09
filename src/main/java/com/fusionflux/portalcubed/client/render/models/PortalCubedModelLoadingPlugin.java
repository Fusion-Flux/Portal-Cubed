package com.fusionflux.portalcubed.client.render.models;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.fusionflux.portalcubed.client.render.models.rendertype.MultiRenderTypeUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.BeforeBake;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;

public enum PortalCubedModelLoadingPlugin implements ModelLoadingPlugin {
	INSTANCE;

	@Override
	public void onInitializeModelLoader(Context ctx) {
		Event<BeforeBake> beforeBake = ctx.modifyModelBeforeBake();
		beforeBake.register(ModelModifier.WRAP_PHASE, MultiRenderTypeWrapper.INSTANCE);
		beforeBake.register(ModelModifier.WRAP_PHASE, EmissiveWrapper.INSTANCE);
	}

	private enum MultiRenderTypeWrapper implements BeforeBake {
		INSTANCE;

		@Override
		public UnbakedModel modifyModelBeforeBake(UnbakedModel model, Context context) {
			if (model instanceof BlockModel blockModel) {
				for (BlockElement element : blockModel.getElements()) {
					RenderMaterial material = ((BlockElementExt) element).portalcubed$getRenderMaterial();
					if (material != null) {
						return new MultiRenderTypeUnbakedModel(blockModel);
					}
				}
			}

			return model;
		}
	}

	private enum EmissiveWrapper implements BeforeBake {
		INSTANCE;

		@Override
		public UnbakedModel modifyModelBeforeBake(UnbakedModel model, Context context) {
			return model;
		}
	}
}
