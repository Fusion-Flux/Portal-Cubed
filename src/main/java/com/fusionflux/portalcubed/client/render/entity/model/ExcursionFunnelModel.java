package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.AnimatedEntityTextures;
import com.fusionflux.portalcubed.client.render.PortalCubedRenderTypes;
import com.fusionflux.portalcubed.client.render.entity.ExcursionFunnelRenderer;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;
import com.fusionflux.portalcubed.mixin.client.TextureAtlasAccessor;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class ExcursionFunnelModel extends Model {
	public static final ResourceLocation TEXTURE = PortalCubed.id("excursion_funnel_beam_forward");
	private ModelPart part;

	public ExcursionFunnelModel(ExcursionFunnelEntity entity) {
		super(texture -> PortalCubedRenderTypes.ANIMATED_TRANSLUCENT_ENTITY);
		build(entity);
	}

	public void build(ExcursionFunnelEntity entity) {
		TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(AnimatedEntityTextures.ATLAS_ID);
		TextureAtlasAccessor atlasAccess = (TextureAtlasAccessor) atlas;
		TextureAtlasSprite sprite = atlas.getSprite(TEXTURE);

		MeshDefinition mesh = new MeshDefinition();
		mesh.getRoot().addOrReplaceChild(
				"bb_main",
				CubeListBuilder.create()
						.texOffs(sprite.getX(), sprite.getY())
						.addBox(
								-15.0F, -16.0F, -15.0F, 30.0F, entity.length, 30.0F,
								CubeDeformation.NONE
						),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		this.part = LayerDefinition.create(mesh, atlasAccess.invokeGetWidth(), atlasAccess.invokeGetHeight()).bakeRoot();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
							   int packedOverlay, float red, float green, float blue, float alpha) {
		this.part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
