package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class VelocityHelperRenderer implements BlockEntityRenderer<VelocityHelperBlockEntity> {
    public VelocityHelperRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(VelocityHelperBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(VelocityHelperBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!PortalCubedClient.hiddenBlocksVisible()) return;
        if (entity.getDestination() != null) {
            final BlockPos offset = entity.getDestination().subtract(entity.getPos());
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
            final MatrixStack.Entry matrix = matrices.peek();
            final Vec3f normal = new Vec3f(Vec3d.of(offset).normalize());
            vertexConsumer
                .vertex(matrix.getModel(), 0.5f, 0.5f, 0.5f)
                .color(0.0f, 0.5f, 1.0f, 1.0f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
            vertexConsumer
                .vertex(matrix.getModel(), offset.getX() + 0.5f, offset.getY() + 0.5f, offset.getZ() + 0.5f)
                .color(1.0f, 0.5f, 0.0f, 1.0f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
        }
    }
}
