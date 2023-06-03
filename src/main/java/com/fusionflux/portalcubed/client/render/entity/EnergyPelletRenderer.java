package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.util.Mth;

public class EnergyPelletRenderer extends ThrownItemRenderer<EnergyPelletEntity> {
    public static Float pelletAlpha = null;

    public EnergyPelletRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, 1f, true);
    }

    @Override
    public void render(EnergyPelletEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (entity.getStartingLife() > 0) {
            pelletAlpha = Mth.clamp(Mth.lerp((float)entity.getLife() / entity.getStartingLife(), 0.25f, 1f), 0f, 1f);
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        pelletAlpha = null;
    }
}
