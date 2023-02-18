package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.entity.DecalEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class DecalRenderer extends EntityRenderer<DecalEntity> {

    public DecalRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(DecalEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(entity.getPitch()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw()));

//        new ModelPart.Cuboid(
//            0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, false, 16, 16
//        ).renderCuboid(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getSolid()), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
//        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);

        new ModelPart.Cuboid(
            0, 0, 0, -8, -8, 0.01f, 16, 16, 0, 0, 0, false, 16, 16
        ).renderCuboid(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

//        final VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
//        consumer
//            .vertex(0, 0, 0)
//            .color(1f, 1f, 1f, 1f)
//            .uv(0f, 0f)
//            .overlay(0, 0)
//            .light(light)
//            .normal(0f, 0f, 1f)
//            .next();
//        consumer
//            .vertex(0, 1, 0)
//            .color(1f, 1f, 1f, 1f)
//            .uv(0f, 1f)
//            .overlay(0, 0)
//            .light(light)
//            .normal(0f, 0f, 1f)
//            .next();
//        consumer
//            .vertex(1, 1, 0)
//            .color(1f, 1f, 1f, 1f)
//            .uv(1f, 1f)
//            .overlay(0, 0)
//            .light(light)
//            .normal(0f, 0f, 1f)
//            .next();
//        consumer
//            .vertex(1, 0, 0)
//            .color(1f, 1f, 1f, 1f)
//            .uv(1f, 0f)
//            .overlay(0, 0)
//            .light(light)
//            .normal(0f, 0f, 1f)
//            .next();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(DecalEntity entity) {
        return entity.getTexture();
    }
}
