package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public abstract class CorePhysicsRenderer<T extends CorePhysicsEntity, M extends EntityModel<T>> extends MobEntityRenderer<T, M> {
    public CorePhysicsRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        RayonIntegration.INSTANCE.multiplyMatrices(matrices, entity, tickDelta);
    }
}
