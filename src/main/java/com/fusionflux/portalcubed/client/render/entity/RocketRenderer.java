package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.block.entity.RocketTurretModel;
import com.fusionflux.portalcubed.client.render.entity.model.RocketModel;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class RocketRenderer extends EntityRenderer<RocketEntity> {
    public static final EntityModelLayer ROCKET_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "rocket"), "main");

    private final RocketModel model;

    public RocketRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new RocketModel(ctx.getPart(ROCKET_LAYER));
    }

    @Override
    public void render(RocketEntity entity, float yaw2, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw2, tickDelta, matrices, vertexConsumers, light);

        matrices.push();

        final float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.getYaw());
        final float pitch = MathHelper.lerpAngleDegrees(tickDelta, entity.prevPitch, entity.getPitch());

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-pitch));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 - yaw));
        matrices.scale(-1, -1, 1);
        matrices.translate(0.0, -1.501, 0.0);

        model.animateModel(entity, 0, 0, tickDelta);
        model.setAngles(entity, 0, 0, entity.age + tickDelta, yaw, pitch);

        final RenderLayer renderLayer = model.getLayer(getTexture(entity));
        if (renderLayer != null) {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            final int overlay2 = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
            model.render(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(RocketEntity entity) {
        return RocketTurretModel.TEXTURE_ACTIVE;
    }
}
