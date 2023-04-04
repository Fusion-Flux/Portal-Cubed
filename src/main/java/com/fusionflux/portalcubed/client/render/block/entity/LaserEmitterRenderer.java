package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.LaserEmitterBlockEntity;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class LaserEmitterRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {
    public LaserEmitterRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(LaserEmitterBlockEntity blockEntity) {
        return blockEntity.getMultiSegments() != null;
    }

    @Override
    public int getRenderDistance() {
        return PortalCubedConfig.maxBridgeLength + 1;
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getMultiSegments() == null) return;
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLightning());
        final MatrixStack.Entry matrix = matrices.peek();
        for (final var segments : entity.getMultiSegments()) {
            for (final var aimDestInfo : segments.rays()) {
                final Vec3f start = new Vec3f(aimDestInfo.start().subtract(Vec3d.of(entity.getPos())));
                final Vec3f end = new Vec3f(aimDestInfo.end().subtract(Vec3d.of(entity.getPos())));
                drawSegments(vertexConsumer, matrix, start, end, 0.1f, 0f, 0f, 0.05f, 0.0f);
                drawSegments(vertexConsumer, matrix, start, end, 1f, 0f, 0f, 0.02f, 0.01f);
                drawSegments(vertexConsumer, matrix, start, end, 1f, 0.5f, 0.5f, 0.01f, 0.02f);
            }
        }
    }

    private void drawSegments(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, Vec3f start, Vec3f end, float r, float g, float b, float size, float offset) {
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, size, 0.0f, 0f, 0f, offset, 0f);
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, 0f, size, 0f, offset, 0f, offset);
        drawSegment(vertexConsumer, matrix, start, end, r, g, b, 0f, 0.0f, size, 0f, offset, 0f);
    }

    private void drawSegment(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, Vec3f start, Vec3f end, float r, float g, float b, float xSize1, float ySize1, float zSize1, float xSize2, float ySize2, float zSize2) {
        vertexConsumer
            .vertex(matrix.getModel(), start.getX() - xSize1 + xSize2, start.getY() - ySize1 + ySize2, start.getZ() - zSize1 + zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), start.getX() + xSize1 + xSize2, start.getY() + ySize1 + ySize2, start.getZ() + zSize1 + zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), end.getX() + xSize1 + xSize2, end.getY() + ySize1 + ySize2, end.getZ() + zSize1 + zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), end.getX() - xSize1 + xSize2, end.getY() - ySize1 + ySize2, end.getZ() - zSize1 + zSize2)
            .color(r, g, b, 1f)
            .next();

        vertexConsumer
            .vertex(matrix.getModel(), start.getX() - xSize1 - xSize2, start.getY() - ySize1 - ySize2, start.getZ() - zSize1 - zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), end.getX() - xSize1 - xSize2, end.getY() - ySize1 - ySize2, end.getZ() - zSize1 - zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), end.getX() + xSize1 - xSize2, end.getY() + ySize1 - ySize2, end.getZ() + zSize1 - zSize2)
            .color(r, g, b, 1f)
            .next();
        vertexConsumer
            .vertex(matrix.getModel(), start.getX() + xSize1 - xSize2, start.getY() + ySize1 - ySize2, start.getZ() + zSize1 - zSize2)
            .color(r, g, b, 1f)
            .next();
    }
}
