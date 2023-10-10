package com.fusionflux.portalcubed.client.render.models.emissive;

import java.util.Collection;

import com.fusionflux.portalcubed.client.render.models.RenderMaterials;
import com.fusionflux.portalcubed.client.render.models.rendertype.MultiRenderTypeBakedModel;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class EmissiveBakedModel extends MultiRenderTypeBakedModel {
	public EmissiveBakedModel(SimpleBakedModel model, Collection<ResourceLocation> emissiveTextures) {
		super(model);
		for (int i = 0; i < this.quads.size(); i++) {
			Triple<BakedQuad, RenderMaterial, Direction> triple = quads.get(i);
			ResourceLocation texture = triple.getLeft().getSprite().contents().name();
			if (emissiveTextures.contains(texture)) {
				RenderMaterial material = triple.getMiddle();
				RenderMaterial emissive = RenderMaterials.get(material.blendMode(), true);
				Triple<BakedQuad, RenderMaterial, Direction> newTriple = Triple.of(triple.getLeft(), emissive, triple.getRight());
				quads.set(i, newTriple);
			}
		}
	}
}
