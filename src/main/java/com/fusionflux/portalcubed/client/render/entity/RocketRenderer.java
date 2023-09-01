package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.block.entity.RocketTurretModel;
import com.fusionflux.portalcubed.client.render.entity.model.RocketModel;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketRenderer extends EntityRenderer<RocketEntity> {
	public static final ModelLayerLocation ROCKET_LAYER = new ModelLayerLocation(id("rocket"), "main");

	private final RocketModel model;

	public RocketRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		model = new RocketModel(ctx.bakeLayer(ROCKET_LAYER));
	}

	@Override
	public void render(RocketEntity entity, float yaw2, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
		super.render(entity, yaw2, tickDelta, matrices, vertexConsumers, light);

		matrices.pushPose();

		final float yaw = Mth.rotLerp(tickDelta, entity.yRotO, entity.getYRot());
		final float pitch = Mth.rotLerp(tickDelta, entity.xRotO, entity.getXRot());

		matrices.mulPose(Axis.XP.rotationDegrees(-pitch));
		matrices.mulPose(Axis.YP.rotationDegrees(180 - yaw));
		matrices.scale(-1, -1, 1);
		matrices.translate(0.0, -1.501, 0.0);

		model.prepareMobModel(entity, 0, 0, tickDelta);
		model.setupAnim(entity, 0, 0, entity.tickCount + tickDelta, yaw, pitch);

		final RenderType renderLayer = model.renderType(getTextureLocation(entity));
		if (renderLayer != null) {
			final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
			final int overlay2 = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
			model.renderToBuffer(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
		}

		matrices.popPose();
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(RocketEntity entity) {
		return RocketTurretModel.TEXTURE_ACTIVE;
	}
}
