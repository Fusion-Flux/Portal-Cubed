package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;

public abstract class CorePhysicsRenderer<T extends CorePhysicsEntity, M extends EntityModel<T>> extends MobRenderer<T, M> {
    public CorePhysicsRenderer(EntityRendererProvider.Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.isCustomNameVisible() && super.shouldShowName(entity);
    }

    @Override
    protected void setupRotations(T entity, PoseStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        RayonIntegration.INSTANCE.multiplyMatrices(matrices, entity, tickDelta);
    }
}
