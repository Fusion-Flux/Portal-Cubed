package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.entity.Fizzleable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public final class EntityEmissiveRendering {

    public static <T extends Entity, M extends EntityModel<T>> void renderEmissive(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, M model, Function<T, ResourceLocation> emissiveTextureGetter) {
        final ResourceLocation texture = emissiveTextureGetter.apply(entity);
        if (texture == null) return;
        var brightness = 1f;
        if (entity instanceof Fizzleable fizzleable) {
            brightness -= Math.min(fizzleable.getFizzleProgress(), 1f);
        }
        final var vertexConsumer = vertexConsumers.getBuffer(RenderType.eyes(emissiveTextureGetter.apply(entity)));
        model.renderToBuffer(matrices, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, brightness, brightness, brightness, 1f);
    }

    public static <T extends Entity, M extends EntityModel<T>> EmissiveFeatureRenderer<T, M> featureRenderer(RenderLayerParent<T, M> featureRendererContext, Function<T, ResourceLocation> emissiveTextureGetter) {
        return new EmissiveFeatureRenderer<>(featureRendererContext, emissiveTextureGetter);
    }

    private static class EmissiveFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

        private final Function<T, ResourceLocation> emissiveTextureGetter;

        private EmissiveFeatureRenderer(RenderLayerParent<T, M> featureRendererContext, Function<T, ResourceLocation> emissiveTextureGetter) {
            super(featureRendererContext);
            this.emissiveTextureGetter = emissiveTextureGetter;
        }

        @Override
        public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity,
                float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
                float headPitch) {
            EntityEmissiveRendering.renderEmissive(matrices, vertexConsumers, entity, getParentModel(), emissiveTextureGetter);
        }

    }

}
