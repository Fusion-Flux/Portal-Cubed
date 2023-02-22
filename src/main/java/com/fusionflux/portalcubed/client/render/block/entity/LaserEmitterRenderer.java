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
        return blockEntity.getSegments() != null;
    }

    @Override
    public int getRenderDistance() {
        return PortalCubedConfig.maxBridgeLength + 1;
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getSegments() == null) return;
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        final MatrixStack.Entry matrix = matrices.peek();
        for (final var aimDestInfo : entity.getSegments().rays()) {
            final Vec3f origin = new Vec3f(aimDestInfo.start().subtract(Vec3d.of(entity.getPos())));
            final Vec3f offset = new Vec3f(aimDestInfo.end().subtract(Vec3d.of(entity.getPos())));
            final Vec3f normal = offset.copy();
            normal.normalize();
            vertexConsumer
                .vertex(matrix.getModel(), origin.getX(), origin.getY(), origin.getZ())
                .color(1f, 0f, 0f, 1f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
            vertexConsumer
                .vertex(matrix.getModel(), offset.getX(), offset.getY(), offset.getZ())
                .color(1f, 0f, 0f, 1f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
        }
    }
}
