package com.fusionflux.portalcubed.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;

@Mixin(SimpleBakedModel.class)
public interface SimpleBakedModelAccessor {
    @Accessor
    List<BakedQuad> getUnculledFaces();

    @Accessor
    Map<Direction, List<BakedQuad>> getCulledFaces();
}
