package com.fusionflux.portalcubed.client.render;

import java.util.function.Function;

import com.fusionflux.portalcubed.client.render.entity.animated_textures.AnimatedEntityTextures;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PortalCubedRenderTypes {
	private static final Function<ResourceLocation, RenderType> animatedTranslucentEntityFactory = Util.memoize(
			texture -> RenderType.create(
					"animated_translucent_entity",
					DefaultVertexFormat.NEW_ENTITY,
					VertexFormat.Mode.QUADS,
					256,
					true,
					true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
							.setTextureState(new TextureStateShard(texture, false, false))
							.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderStateShard.NO_CULL)
							.setLightmapState(RenderStateShard.LIGHTMAP)
							.setOverlayState(RenderStateShard.OVERLAY)
							.createCompositeState(true)
			)
	);

	public static RenderType getAnimatedTranslucentEntity(ResourceLocation texture) {
		return animatedTranslucentEntityFactory.apply(texture);
	}
}
