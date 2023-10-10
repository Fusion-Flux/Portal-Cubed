package com.fusionflux.portalcubed.client.render.models.emissive;

import java.util.Collection;

import com.fusionflux.portalcubed.client.render.models.rendertype.MultiRenderTypeBakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.AfterBake;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;

public record EmissiveWrapper(EmissiveData data) implements AfterBake {
	@Override
	@Nullable
	public BakedModel modifyModelAfterBake(BakedModel model, Context context) {
		Collection<ResourceLocation> textures = data.getEmissiveTexturesForModel(context.id());
		if (!textures.isEmpty()) {
			SimpleBakedModel simple = getSimpleBakedModel(model);
			if (simple != null) {
				return new EmissiveBakedModel(simple, textures);
			}
		}
		return model;
	}

	private SimpleBakedModel getSimpleBakedModel(BakedModel model) {
		if (model instanceof SimpleBakedModel simple) {
			return simple;
		} else if (model instanceof MultiRenderTypeBakedModel multi) {
			return (SimpleBakedModel) multi.getWrappedModel();
		}
		return null;
	}
}
