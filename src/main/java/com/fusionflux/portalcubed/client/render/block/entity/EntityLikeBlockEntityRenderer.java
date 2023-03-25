package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.EntityLikeBlockEntity;
import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public abstract class EntityLikeBlockEntityRenderer<T extends EntityLikeBlockEntity, M extends EntityLikeBlockEntityModel<T>> implements BlockEntityRenderer<T> {
    private final EntityModelLoader modelLoader;
    private final Map<T, Pair<BlockEntityWrapperEntity<T>, M>> wrapperModelPairs = new WeakHashMap<>();
    private final Function<ModelPart, M> modelFactory;

    protected EntityLikeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx, Function<ModelPart, M> modelFactory) {
        modelLoader = ctx.getLayerRenderDispatcher();
        this.modelFactory = modelFactory;
    }

    private Pair<BlockEntityWrapperEntity<T>, M> createWrapperModelPair(T entity) {
        return Pair.of(new BlockEntityWrapperEntity<>(entity), modelFactory.apply(modelLoader.getModelPart(getModelLayer())));
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.scale(-1, -1, 1);
        matrices.translate(0.5, -1.501, -0.5);

        final var wrapperModelPair = wrapperModelPairs.computeIfAbsent(entity, this::createWrapperModelPair);
        final BlockEntityWrapperEntity<T> wrapper = wrapperModelPair.key();
        final M model = wrapperModelPair.value();

        final float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.getYaw());
        final float pitch = MathHelper.lerpAngleDegrees(tickDelta, entity.prevPitch, entity.getPitch());

        model.animateModel(wrapper, 0, 0, tickDelta);
        model.setAngles(wrapper, 0, 0, entity.getAge() + tickDelta, yaw, pitch);

        final RenderLayer renderLayer = model.getLayer(model.getTexture(entity));
        if (renderLayer != null) {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            final int overlay2 = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
            model.render(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
            EntityEmissiveRendering.renderEmissive(matrices, vertexConsumers, wrapper, model, e -> model.getEmissiveTexture(e.getBlockEntity()));
        }

        matrices.pop();
    }

    protected abstract EntityModelLayer getModelLayer();
}
