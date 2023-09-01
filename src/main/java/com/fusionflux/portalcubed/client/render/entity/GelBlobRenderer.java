package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumSet;

public class GelBlobRenderer extends EntityRenderer<GelBlobEntity> {
	private final ModelPart.Cube cube;

	public GelBlobRenderer(EntityRendererProvider.Context context) {
		super(context);
		cube = new ModelPart.Cube(
			0, 0, // U, V
			-8, 0, -8, // X, Y, Z
			16, 16, 16, // XS, YS, ZS
			0, 0, 0, // Extra XS, Extra YS, Extra ZS
			false, // Mirror
			1, 1, // U width, V height
			EnumSet.allOf(Direction.class)
		);
	}

	// Math from https://github.com/Tectato/Vectorientation/blob/2bfe2fc2d2c36f8af3550df09d1b5d7938869a70/src/main/java/vectorientation/mixin/FallingBlockRendererMixin.java
	@Override
	public void render(GelBlobEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
		final VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entitySolid(getTextureLocation(entity)));
		matrices.pushPose();

		final Vector3f vel = entity.getDeltaMovement().toVector3f();
		final float y = (vel.y() - 0.04f * tickDelta) * 0.98f;
		float speed = (float)Math.sqrt(vel.x() * vel.x() + y * y + vel.z() * vel.z());
		vel.normalize();
		final float angle = (float)Math.acos(Mth.clamp(y, -1, 1));
		vel.set(-1 * vel.z(), 0, vel.x());
		vel.normalize();
		final Quaternionf rot = new Quaternionf().setAngleAxis(-angle, vel.x, vel.y, vel.z);
		matrices.translate(0, 0.5, 0);
		matrices.mulPose(rot);
		speed += 0.75f;
		matrices.scale(1 / speed, speed, 1 / speed);
		matrices.translate(0, -0.5, 0);

		final float scale = entity.getScale();
		matrices.scale(scale, scale, scale);

		cube.compile(
			matrices.last(), consumer,
			light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1
		);
		matrices.popPose();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(GelBlobEntity entity) {
		return entity.getTexture();
	}
}
