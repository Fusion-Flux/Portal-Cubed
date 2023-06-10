package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketTurretRenderer extends EntityLikeBlockEntityRenderer<RocketTurretBlockEntity, RocketTurretModel> {
    public static final ModelLayerLocation ROCKET_TURRET_LAYER = new ModelLayerLocation(id("rocket_turret"), "main");

    public RocketTurretRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, RocketTurretModel::new);
    }

    @Override
    public boolean shouldRenderOffScreen(RocketTurretBlockEntity blockEntity) {
        return blockEntity.aimDests != null;
    }

    @Override
    public int getViewDistance() {
        return 128; // So that the whole laser can be seen. See the raycast in RocketTurretBlockEntity.java.
    }

    @Override
    public void render(RocketTurretBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        if (entity.aimDests == null) return;

        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lines());
        final PoseStack.Pose matrix = matrices.last();
        for (final var aimDestInfo : entity.aimDests) {
            final Vector3f origin = aimDestInfo.getA().subtract(Vec3.atLowerCornerOf(entity.getBlockPos())).toVector3f();
            final Vector3f offset = aimDestInfo.getB().subtract(Vec3.atLowerCornerOf(entity.getBlockPos())).toVector3f();
            final Vector3f normal = new Vector3f(offset).sub(origin).normalize();
            vertexConsumer
                .vertex(matrix.pose(), origin.x(), origin.y(), origin.z())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.normal(), normal.x(), normal.y(), normal.z())
                .endVertex();
            vertexConsumer
                .vertex(matrix.pose(), offset.x(), offset.y(), offset.z())
                .color(130 / 255f, 200 / 255f, 230 / 255f, 0.25f)
                .normal(matrix.normal(), normal.x(), normal.y(), normal.z())
                .endVertex();
        }
    }

    @Override
    protected ModelLayerLocation getModelLayer() {
        return ROCKET_TURRET_LAYER;
    }
}
