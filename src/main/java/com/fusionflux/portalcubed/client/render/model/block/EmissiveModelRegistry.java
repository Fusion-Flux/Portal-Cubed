package com.fusionflux.portalcubed.client.render.model.block;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;

public final class EmissiveModelRegistry {

    private static final Map<Identifier, Function<BakedModel, EmissiveBakedModel>> wrappers = new Object2ObjectOpenHashMap<>();

    private EmissiveModelRegistry() {}

    public static Optional<BakedModel> wrapModel(Identifier modelId, BakedModel model) {
        final Function<BakedModel, EmissiveBakedModel> wrapper = wrappers.get(new Identifier(modelId.getNamespace(), modelId.getPath()));
        if (wrapper != null) return Optional.of(wrapper.apply(model));
        return Optional.empty();
    }

    public static void register(Identifier modelId, Identifier spriteToMakeEmissive) {
        wrappers.put(modelId, bakedModel -> new EmissiveBakedModel(bakedModel, spriteToMakeEmissive));
    }

}
