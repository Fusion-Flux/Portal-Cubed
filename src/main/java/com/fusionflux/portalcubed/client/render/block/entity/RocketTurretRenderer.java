package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Map;
import java.util.WeakHashMap;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketTurretRenderer implements BlockEntityRenderer<RocketTurretBlockEntity> {
    public static final EntityModelLayer ROCKET_TURRET_LAYER = new EntityModelLayer(id("rocket_turret"), "main");

    private final EntityModelLoader modelLoader;
    private final Map<RocketTurretBlockEntity, Pair<BlockEntityWrapperEntity<RocketTurretBlockEntity>, RocketTurretModel>> wrapperModelPairs = new WeakHashMap<>();

    public RocketTurretRenderer(BlockEntityRendererFactory.Context ctx) {
        modelLoader = ctx.getLayerRenderDispatcher();
    }

    @Override
    public boolean rendersOutsideBoundingBox(RocketTurretBlockEntity blockEntity) {
        return blockEntity.aimDests != null;
    }

    @Override
    public int getRenderDistance() {
        return 128; // So that the whole laser can be seen. See the raycast in RocketTurretBlockEntity.java.
    }

    private Pair<BlockEntityWrapperEntity<RocketTurretBlockEntity>, RocketTurretModel> createWrapperModelPair(RocketTurretBlockEntity entity) {
        return Pair.of(new BlockEntityWrapperEntity<>(entity), new RocketTurretModel(modelLoader.getModelPart(ROCKET_TURRET_LAYER)));
    }

    @Override
    public void render(RocketTurretBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.scale(-1, -1, 1);
        matrices.translate(0.5, -1.501, -0.5);

        final var wrapperModelPair = wrapperModelPairs.computeIfAbsent(entity, this::createWrapperModelPair);
        final BlockEntityWrapperEntity<RocketTurretBlockEntity> wrapper = wrapperModelPair.key();
        final RocketTurretModel model = wrapperModelPair.value();

        final float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.lastYaw, entity.getYaw());
        final float pitch = MathHelper.lerpAngleDegrees(tickDelta, entity.lastPitch, entity.getPitch());

        model.animateModel(wrapper, 0, 0, tickDelta);
        model.setAngles(wrapper, 0, 0, entity.getAge() + tickDelta, yaw, pitch);

        final RenderLayer renderLayer = model.getLayer(model.getTexture(entity));
        if (renderLayer != null) {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            final int overlay2 = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
            model.render(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
            if (!(entity.deactivatingAnimation.isAnimating() && entity.deactivatingAnimation.m_hkidhtpg() >= 2000)) {
                EntityEmissiveRendering.renderEmissive(matrices, vertexConsumers, wrapper, model, e -> model.getEmissiveTexture(entity));
            }
        }

        matrices.pop();

        if (entity.aimDests == null) return;

        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        final MatrixStack.Entry matrix = matrices.peek();
        for (final var aimDestInfo : entity.aimDests) {
            final Vec3f origin = new Vec3f(aimDestInfo.getLeft().subtract(Vec3d.of(entity.getPos())));
            final Vec3f offset = new Vec3f(aimDestInfo.getRight().subtract(Vec3d.of(entity.getPos())));
            final Vec3f normal = offset.copy();
            normal.normalize();
            vertexConsumer
                .vertex(matrix.getModel(), origin.getX(), origin.getY(), origin.getZ())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
            vertexConsumer
                .vertex(matrix.getModel(), offset.getX(), offset.getY(), offset.getZ())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
                .next();
        }
    }
}
