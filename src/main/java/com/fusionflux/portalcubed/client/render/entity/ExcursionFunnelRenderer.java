package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.ExcursionFunnelModel;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ExcursionFunnelRenderer extends EntityRenderer<ExcursionFunnelEntity> {
	public ExcursionFunnelRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(ExcursionFunnelEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (entity.model == null)
			entity.model = new ExcursionFunnelModel(entity);
		ExcursionFunnelModel model = entity.model;
		VertexConsumer consumer = buffer.getBuffer(model.renderType(getTextureLocation(entity)));

		poseStack.pushPose();
		poseStack.mulPose(entity.getFacing().getRotation());
		model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
	}

	@Override
	@NotNull
	public ResourceLocation getTextureLocation(ExcursionFunnelEntity entity) {
		return entity.isReversed() ? ExcursionFunnelModel.REVERSED_TEXTURE : ExcursionFunnelModel.TEXTURE;
	}
}
