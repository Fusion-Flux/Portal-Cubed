package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.LaserEmitterBlockEntity;
import com.fusionflux.portalcubed.PortalCubedConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LaserEmitterRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {
    public LaserEmitterRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public boolean shouldRenderOffScreen(LaserEmitterBlockEntity blockEntity) {
        return blockEntity.getMultiSegments() != null;
    }

    @Override
    public int getViewDistance() {
        return PortalCubedConfig.maxBridgeLength + 1;
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity.getMultiSegments() == null) return;
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lightning());
        final PoseStack.Pose matrix = matrices.last();
        for (final var segments : entity.getMultiSegments()) {
            for (final var aimDestInfo : segments.rays()) {
                final Vector3f start = new Vector3f(aimDestInfo.start().subtract(Vec3.atLowerCornerOf(entity.getBlockPos())));
                final Vector3f end = new Vector3f(aimDestInfo.end().subtract(Vec3.atLowerCornerOf(entity.getBlockPos())));
                drawSegments(vertexConsumer, matrix, start, end, 0.1f, 0f, 0f, 0.05f, 0.0f);
                drawSegments(vertexConsumer, matrix, start, end, 1f, 0f, 0f, 0.02f, 0.01f);
                drawSegments(vertexConsumer, matrix, start, end, 1f, 0.5f, 0.5f, 0.01f, 0.02f);
            }
        }
    }

    private void drawSegments(VertexConsumer vertexConsumer, PoseStack.Pose matrix, Vector3f start, Vector3f end, float r, float g, float b, float size, float offset) {
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, size, 0.0f, 0f, 0f, offset, 0f);
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, 0f, size, 0f, offset, 0f, offset);
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, 0f, 0.0f, size, 0f, offset, 0f);
    }

    private void drawSegment(VertexConsumer vertexConsumer, PoseStack.Pose matrix, Vector3f start, Vector3f end, float r, float g, float b, float xSize1, float ySize1, float zSize1, float xSize2, float ySize2, float zSize2) {
        vertexConsumer
            .vertex(matrix.pose(), start.x() - xSize1 + xSize2, start.y() - ySize1 + ySize2, start.z() - zSize1 + zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), start.x() + xSize1 + xSize2, start.y() + ySize1 + ySize2, start.z() + zSize1 + zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), end.x() + xSize1 + xSize2, end.y() + ySize1 + ySize2, end.z() + zSize1 + zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), end.x() - xSize1 + xSize2, end.y() - ySize1 + ySize2, end.z() - zSize1 + zSize2)
            .color(r, g, b, 1f)
            .endVertex();

        vertexConsumer
            .vertex(matrix.pose(), start.x() - xSize1 - xSize2, start.y() - ySize1 - ySize2, start.z() - zSize1 - zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), end.x() - xSize1 - xSize2, end.y() - ySize1 - ySize2, end.z() - zSize1 - zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), end.x() + xSize1 - xSize2, end.y() + ySize1 - ySize2, end.z() + zSize1 - zSize2)
            .color(r, g, b, 1f)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), start.x() + xSize1 - xSize2, start.y() + ySize1 - ySize2, start.z() + zSize1 - zSize2)
            .color(r, g, b, 1f)
            .endVertex();
    }
}
