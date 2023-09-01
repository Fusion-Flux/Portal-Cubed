package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class VelocityHelperRenderer implements BlockEntityRenderer<VelocityHelperBlockEntity> {
	public VelocityHelperRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public boolean shouldRenderOffScreen(VelocityHelperBlockEntity blockEntity) {
		return true;
	}

	@Override
	public void render(VelocityHelperBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		if (!PortalCubedClient.hiddenBlocksVisible()) return;
		if (entity.getDestination() != null) {
			final BlockPos offset = entity.getDestination().subtract(entity.getBlockPos());
			final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lines());
			final PoseStack.Pose matrix = matrices.last();
			final Vector3f normal = Vec3.atLowerCornerOf(offset).normalize().toVector3f();
			vertexConsumer
				.vertex(matrix.pose(), 0.5f, 0.5f, 0.5f)
				.color(0.0f, 0.5f, 1.0f, 1.0f)
				.normal(matrix.normal(), normal.x(), normal.y(), normal.z())
				.endVertex();
			vertexConsumer
				.vertex(matrix.pose(), offset.getX() + 0.5f, offset.getY() + 0.5f, offset.getZ() + 0.5f)
				.color(1.0f, 0.5f, 0.0f, 1.0f)
				.normal(matrix.normal(), normal.x(), normal.y(), normal.z())
				.endVertex();
		}
	}
}
