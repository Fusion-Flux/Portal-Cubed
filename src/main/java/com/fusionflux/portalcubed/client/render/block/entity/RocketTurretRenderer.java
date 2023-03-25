package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketTurretRenderer extends EntityLikeBlockEntityRenderer<RocketTurretBlockEntity, RocketTurretModel> {
    public static final EntityModelLayer ROCKET_TURRET_LAYER = new EntityModelLayer(id("rocket_turret"), "main");

    public RocketTurretRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx, RocketTurretModel::new);
    }

    @Override
    public boolean rendersOutsideBoundingBox(RocketTurretBlockEntity blockEntity) {
        return blockEntity.aimDests != null;
    }

    @Override
    public int getRenderDistance() {
        return 128; // So that the whole laser can be seen. See the raycast in RocketTurretBlockEntity.java.
    }

    @Override
    public void render(RocketTurretBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        if (entity.aimDests == null) return;

        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        final MatrixStack.Entry matrix = matrices.peek();
        for (final var aimDestInfo : entity.aimDests) {
            final Vec3f origin = new Vec3f(aimDestInfo.getLeft().subtract(Vec3d.of(entity.getPos())));
            final Vec3f offset = new Vec3f(aimDestInfo.getRight().subtract(Vec3d.of(entity.getPos())));
            final Vec3f normal = offset.copy();
            normal.subtract(origin);
            normal.normalize();
            vertexConsumer
                .vertex(matrix.getModel(), origin.getX(), origin.getY(), origin.getZ())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
            vertexConsumer
                .vertex(matrix.getModel(), offset.getX(), offset.getY(), offset.getZ())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
        }
    }

    @Override
    protected EntityModelLayer getModelLayer() {
        return ROCKET_TURRET_LAYER;
    }
}
