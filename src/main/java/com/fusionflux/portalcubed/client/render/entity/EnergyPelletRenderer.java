package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class EnergyPelletRenderer extends FlyingItemEntityRenderer<EnergyPelletEntity> {
    public static Float pelletAlpha = null;

    public EnergyPelletRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, 1f, true);
    }

    @Override
    public void render(EnergyPelletEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.getStartingLife() > 0) {
            pelletAlpha = MathHelper.clamp(MathHelper.lerp((float)entity.getLife() / entity.getStartingLife(), 0.25f, 1f), 0f, 1f);
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        pelletAlpha = null;
    }
}
