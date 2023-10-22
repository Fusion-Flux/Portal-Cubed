package com.fusionflux.portalcubed.client.render.models.emissive;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.DataLoader;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public enum EmissiveLoader implements DataLoader<EmissiveData> {
	INSTANCE;

	public static final ResourceLocation EMISSIVES_JSON_LOCATION = PortalCubed.id("emissives.json");

	@Override
	public CompletableFuture<EmissiveData> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> manager.getResource(EMISSIVES_JSON_LOCATION).map(resource -> {
			try {
				Multimap<ResourceLocation, ResourceLocation> modelToTextures = HashMultimap.create();
				GsonHelper.parse(resource.openAsReader()).asMap().forEach((model, element) -> {
					ResourceLocation modelId = getId(model);
					List<ResourceLocation> textures = getTextures(element);
					modelToTextures.putAll(modelId, textures);
				});
				return new EmissiveData(modelToTextures);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).orElseThrow(), executor);
	}

	private static List<ResourceLocation> getTextures(JsonElement element) {
		if (element instanceof JsonArray array) {
			return array.asList().stream().map(JsonElement::getAsString).map(EmissiveLoader::getId).toList();
		} else if (element instanceof JsonPrimitive) {
			return List.of(getId(element.getAsString()));
		}
		return List.of();
	}

	private static ResourceLocation getId(String path) {
		if (path.contains(":")) {
			return new ResourceLocation(path);
		} else {
			return PortalCubed.id(path);
		}
	}
}
