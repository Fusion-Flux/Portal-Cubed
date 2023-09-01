package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.EntityLikeBlockEntity;
import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public abstract class EntityLikeBlockEntityRenderer<T extends EntityLikeBlockEntity, M extends EntityLikeBlockEntityModel<T>> implements BlockEntityRenderer<T> {
	private final EntityModelSet modelLoader;
	private final Map<T, Pair<BlockEntityWrapperEntity<T>, M>> wrapperModelPairs = new WeakHashMap<>();
	private final Function<ModelPart, M> modelFactory;

	protected EntityLikeBlockEntityRenderer(BlockEntityRendererProvider.Context ctx, Function<ModelPart, M> modelFactory) {
		modelLoader = ctx.getModelSet();
		this.modelFactory = modelFactory;
	}

	private Pair<BlockEntityWrapperEntity<T>, M> createWrapperModelPair(T entity) {
		return Pair.of(new BlockEntityWrapperEntity<>(entity), modelFactory.apply(modelLoader.bakeLayer(getModelLayer())));
	}

	@Override
	public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		matrices.pushPose();

		matrices.mulPose(Axis.YP.rotationDegrees(180));
		matrices.scale(-1, -1, 1);
		matrices.translate(0.5, -1.501, -0.5);

		final var wrapperModelPair = wrapperModelPairs.computeIfAbsent(entity, this::createWrapperModelPair);
		final BlockEntityWrapperEntity<T> wrapper = wrapperModelPair.key();
		final M model = wrapperModelPair.value();

		final float yaw = Mth.rotLerp(tickDelta, entity.prevYaw, entity.getYaw());
		final float pitch = Mth.rotLerp(tickDelta, entity.prevPitch, entity.getPitch());

		model.prepareMobModel(wrapper, 0, 0, tickDelta);
		model.setupAnim(wrapper, 0, 0, entity.getAge() + tickDelta, yaw, pitch);

		final RenderType renderLayer = model.renderType(model.getTexture(entity));
		if (renderLayer != null) {
			final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
			final int overlay2 = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
			model.renderToBuffer(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
			EntityEmissiveRendering.renderEmissive(matrices, vertexConsumers, wrapper, model, e -> model.getEmissiveTexture(e.getBlockEntity()));
		}

		matrices.popPose();
	}

	protected abstract ModelLayerLocation getModelLayer();
}
