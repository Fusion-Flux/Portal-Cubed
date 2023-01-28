package com.fusionflux.portalcubed.client.render.model.block;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;

public final class EmissiveSpriteRegistry {

	private static final List<Identifier> sprites = new ObjectArrayList<>();

	public static boolean isEmissive(Identifier spriteId) {
		return sprites.contains(spriteId);
	}

	public static void register(Identifier modelId, Identifier spriteId) {
        EmissiveBakedModel.register(modelId);
        register(spriteId);
	}

	public static void register(Identifier spriteId) {
		sprites.add(spriteId);
	}

}
