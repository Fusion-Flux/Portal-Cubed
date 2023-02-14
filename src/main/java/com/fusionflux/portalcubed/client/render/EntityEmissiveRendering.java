package com.fusionflux.portalcubed.client.render;

import java.util.function.Function;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;

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

public final class EntityEmissiveRendering {

    public static <T extends Entity, M extends EntityModel<T>> void renderEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, M model, Function<T, Identifier> emissiveTextureGetter) {
        var brightness = 1f;
        if (entity instanceof CorePhysicsEntity physicsEntity) {
            brightness -= Math.min(physicsEntity.getFizzleProgress(), 1f);
        }
        final var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(emissiveTextureGetter.apply(entity)));
        model.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, brightness, brightness, brightness, 1f);
    }

    public static <T extends Entity, M extends EntityModel<T>> EmissiveFeatureRenderer<T, M> featureRenderer(FeatureRendererContext<T, M> featureRendererContext, Function<T, Identifier> emissiveTextureGetter) {
        return new EmissiveFeatureRenderer<>(featureRendererContext, emissiveTextureGetter);
    }

    private static class EmissiveFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

        private final Function<T, Identifier> emissiveTextureGetter;

        private EmissiveFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, Function<T, Identifier> emissiveTextureGetter) {
            super(featureRendererContext);
            this.emissiveTextureGetter = emissiveTextureGetter;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
                float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
                float headPitch) {
            EntityEmissiveRendering.renderEmissive(matrices, vertexConsumers, entity, getContextModel(), emissiveTextureGetter);
        }

    }

}
