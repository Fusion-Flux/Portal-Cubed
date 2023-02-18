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
import net.minecraft.util.math.MathHelper;
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

        float alpha = 1f;
        if (entity.getDuration() > 0 && entity.age + 100 >= entity.getDuration()) {
            final float past100 = (entity.age + tickDelta) - entity.getDuration() + 100;
            alpha = 1f - MathHelper.clamp(past100 / 100f, 0f, 1f);
        }

        new ModelPart.Cuboid(
            0, 0, 0, -8, -8, 0.01f, 16, 16, 0, 0, 0, false, 16, 16
        ).renderCuboid(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, alpha);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(DecalEntity entity) {
        return entity.getTexture();
    }
}
