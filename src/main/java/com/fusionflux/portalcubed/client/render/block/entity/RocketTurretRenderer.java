package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.Map;
import java.util.WeakHashMap;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketTurretRenderer implements BlockEntityRenderer<RocketTurretBlockEntity> {
    public static final EntityModelLayer ROCKET_TURRET_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "rocket_turret"), "main");
    public static final Identifier TEXTURE = id("textures/block/rocket_turret.png");

    private final EntityModelLoader modelLoader;
    private final Map<RocketTurretBlockEntity, RocketTurretModel> models = new WeakHashMap<>();

    public RocketTurretRenderer(BlockEntityRendererFactory.Context ctx) {
        modelLoader = ctx.getLayerRenderDispatcher();
    }

    @Override
    public void render(RocketTurretBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.scale(-1, -1, 1);
        matrices.translate(0.5, -1.501, -0.5);

        final BlockEntityWrapperEntity<RocketTurretBlockEntity> wrapper = new BlockEntityWrapperEntity<>(entity);
        final RocketTurretModel model = models.computeIfAbsent(
            entity, e -> new RocketTurretModel(modelLoader.getModelPart(ROCKET_TURRET_LAYER))
        );

        model.animateModel(wrapper, 0, 0, tickDelta);
        model.setAngles(wrapper, 0, 0, entity.getAge() + tickDelta, entity.getYaw(), entity.getPitch());

        final RenderLayer renderLayer = model.getLayer(TEXTURE);
        if (renderLayer != null) {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            final int overlay2 = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
            model.render(matrices, vertexConsumer, light, overlay2, 1, 1, 1, 1);
        }

        matrices.pop();
    }
}
