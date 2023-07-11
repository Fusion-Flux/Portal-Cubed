package com.fusionflux.portalcubed.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class PortalCubedRenderTypes {
	public static final RenderStateShard.TextureStateShard ANIMATED_ENTITY_ATLAS = new RenderStateShard.TextureStateShard(
			AnimatedEntityTextures.ATLAS_ID, false, false
	);

	public static final RenderType ANIMATED_TRANSLUCENT_ENTITY = RenderType.create(
			"animated_translucent_entity",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			true,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
					.setTextureState(ANIMATED_ENTITY_ATLAS)
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.LIGHTMAP)
					.setOverlayState(RenderStateShard.OVERLAY)
					.createCompositeState(true)
	);
}
