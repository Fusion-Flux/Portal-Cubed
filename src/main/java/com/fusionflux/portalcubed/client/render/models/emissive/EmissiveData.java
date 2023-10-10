package com.fusionflux.portalcubed.client.render.models.emissive;

import java.util.Collection;

import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;

public record EmissiveData(Multimap<ResourceLocation, ResourceLocation> map) {
	public Collection<ResourceLocation> getEmissiveTexturesForModel(ResourceLocation modelId) {
		return map.get(modelId);
	}
}
