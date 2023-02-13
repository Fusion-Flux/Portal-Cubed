package com.fusionflux.portalcubed.client.render.block;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;

import java.util.List;

public final class EmissiveSpriteRegistry {

    private static final List<Identifier> SPRITES = new ObjectArrayList<>();

    public static boolean isEmissive(Identifier spriteId) {
        return SPRITES.contains(spriteId);
    }

    public static void register(Identifier modelId, Identifier spriteId) {
        EmissiveBakedModel.register(modelId);
        register(spriteId);
    }

    public static void register(Identifier spriteId) {
        SPRITES.add(spriteId);
    }

}
