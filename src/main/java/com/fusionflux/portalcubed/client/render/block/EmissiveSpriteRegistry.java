package com.fusionflux.portalcubed.client.render.block;

import com.fusionflux.portalcubed.client.render.models.emissive.EmissiveBakedModel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class EmissiveSpriteRegistry {

	private static final List<ResourceLocation> SPRITES = new ObjectArrayList<>();

	public static boolean isEmissive(ResourceLocation spriteId) {
		return SPRITES.contains(spriteId);
	}

	public static void register(ResourceLocation modelId, ResourceLocation spriteId) {
//		EmissiveBakedModel.register(modelId);
		register(spriteId);
	}

	public static void register(ResourceLocation spriteId) {
		SPRITES.add(spriteId);
	}

}
