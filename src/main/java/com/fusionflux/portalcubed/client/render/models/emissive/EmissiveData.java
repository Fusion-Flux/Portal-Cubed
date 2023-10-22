package com.fusionflux.portalcubed.client.render.models.emissive;

import java.util.Collection;

import com.google.common.collect.Multimap;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public record EmissiveData(Multimap<ResourceLocation, ResourceLocation> map) {
	public Collection<ResourceLocation> getEmissiveTexturesForModel(ResourceLocation id) {
		if (id.toString().contains("energy_pellet"))
			System.out.println(id);
		if (id instanceof ModelResourceLocation modelId && modelId.getVariant().equals("inventory")) {
			ResourceLocation sourceFormat = modelId.withPrefix("item/"); // also removes variant
			if (id.toString().contains("energy_pellet"))
				System.out.println(sourceFormat);
			return map.get(sourceFormat);
		}
		return map.get(id);
	}
}
