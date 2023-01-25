package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public abstract class EmissiveFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public EmissiveFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {
        float brightness = 1f;
        if (entity instanceof CorePhysicsEntity physicsEntity) {
            brightness -= Math.min(physicsEntity.getFizzleProgress(), 1f);
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(this.getEmissiveTexture(entity)));
        this.getContextModel().render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, brightness, brightness, brightness, brightness);
    }

    public abstract Identifier getEmissiveTexture(T entity);

}
