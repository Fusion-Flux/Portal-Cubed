package com.fusionflux.portalcubed.client.render.models.rendertype;

import java.util.Collection;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public record MultiRenderTypeUnbakedModel(BlockModel wrapped) implements UnbakedModel {
	@Override
	@NotNull
	public Collection<ResourceLocation> getDependencies() {
		return wrapped.getDependencies();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
		wrapped.resolveParents(resolver);
	}

	@Nullable
	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
		BakedModel baked = wrapped.bake(baker, spriteGetter, state, location);
		return baked instanceof SimpleBakedModel simple ? new MultiRenderTypeBakedModel(simple) : baked;
	}
}
