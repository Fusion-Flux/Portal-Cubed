package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.entity.model.ExperimentalPortalModel;
import com.fusionflux.portalcubed.client.render.portal.PortalRenderer;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class ExperimentalPortalRenderer extends EntityRenderer<ExperimentalPortal> {
    private static final ResourceLocation SQUARE_TEXTURE = id("textures/entity/portal_square_outline_closed.png");
    private static final ResourceLocation ROUND_TEXTURE  = id("textures/entity/portal_oval_outline_closed.png");
    private static final ResourceLocation SQUARE_TEXTURE_TRACER = id("textures/entity/portal_tracer_square.png");
    private static final ResourceLocation ROUND_TEXTURE_TRACER = id("textures/entity/portal_tracer_oval.png");
    protected final ExperimentalPortalModel model = new ExperimentalPortalModel(Minecraft.getInstance().getEntityModels().bakeLayer(ExperimentalPortalModel.MAIN_LAYER));

    public static boolean renderingTracers = false;

    public ExperimentalPortalRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@NotNull ExperimentalPortal entity, float yaw, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(entity.getYRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(entity.getXRot()));
        matrices.mulPose(Axis.ZN.rotationDegrees(entity.getRoll()));

        int color = entity.getColor() * -1;
        if (color == -16383998) {
            color = 1908001;
        }
        if (color == 16383998) {
            color = -1908001;
        }
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;

        final float progress = (entity.tickCount + tickDelta) / 2.5f;
        if (progress <= 1) {
            matrices.scale(progress, progress, progress);
        }

        renderPortal(matrices, vertexConsumers, entity, light, r, g, b, tickDelta);

        matrices.popPose();

        if (PortalCubedConfig.crossPortalEntityRendering) {
            renderOtherEntities(entity, matrices, tickDelta, vertexConsumers, light);
        }

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && !entity.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            renderAxes(entity, matrices, vertexConsumers.getBuffer(RenderType.lines()));
        }
    }

    private void renderPortal(
        PoseStack poseStack,
        MultiBufferSource vertexConsumers,
        ExperimentalPortal entity,
        int light,
        int r,
        int g,
        int b,
        float tickDelta
    ) {
        final PortalRenderer renderer = PortalCubedClient.getRenderer();
        final boolean renderPortal = !renderingTracers && renderer.enabled(entity);
        if (renderPortal) {
            renderer.preRender(entity, tickDelta, poseStack);
        }
        model.renderToBuffer(poseStack, vertexConsumers.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY, r, g, b, 1F);
        if (renderPortal) {
            renderer.postRender(entity, tickDelta, poseStack);
        }
    }

    private void renderOtherEntities(ExperimentalPortal entity, PoseStack poseStack, float tickDelta, MultiBufferSource buffer, int packedLight) {
        if (renderingTracers || !entity.getActive()) return;
        final UUID otherUuid = entity.getLinkedPortalUUID().orElse(null);
        if (otherUuid == null || !(((Accessors)entity.level).getEntity(otherUuid) instanceof ExperimentalPortal otherPortal)) return;
        final double oplx = Mth.lerp(tickDelta, otherPortal.xOld, otherPortal.getX());
        final double oply = Mth.lerp(tickDelta, otherPortal.yOld, otherPortal.getY());
        final double oplz = Mth.lerp(tickDelta, otherPortal.zOld, otherPortal.getZ());
        final List<Entity> otherEntities = otherPortal.level.getEntities(otherPortal, otherPortal.getBoundingBox(), e -> !e.isInvisible());
        final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        final boolean renderHitboxes = dispatcher.shouldRenderHitBoxes();
        dispatcher.setRenderHitBoxes(false);
        for (final Entity otherEntity : otherEntities) {
            poseStack.pushPose();
            poseStack.mulPose(otherPortal.getRotationQuat().toQuaternionf());
            dispatcher.render(
                otherEntity,
                Mth.lerp(tickDelta, otherEntity.xOld, otherEntity.getX()) - oplx,
                Mth.lerp(tickDelta, otherEntity.yOld, otherEntity.getY()) - oply,
                Mth.lerp(tickDelta, otherEntity.zOld, otherEntity.getZ()) - oplz,
                Mth.lerp(tickDelta, otherEntity.yRotO, otherEntity.getYRot()),
                tickDelta, poseStack, buffer, packedLight
            );
            poseStack.popPose();
        }
        dispatcher.setRenderHitBoxes(renderHitboxes);
    }

    private void renderAxes(ExperimentalPortal entity, PoseStack matrices, VertexConsumer vertices) {
        final PoseStack.Pose entry = matrices.last();
        renderAxis(entry, vertices, entity.getNormal());
        entity.getAxisW().ifPresent(axisW -> renderAxis(entry, vertices, axisW));
        entity.getAxisH().ifPresent(axisH -> renderAxis(entry, vertices, axisH));
    }

    private void renderAxis(PoseStack.Pose entry, VertexConsumer vertices, Vec3 axis) {
        vertices
            .vertex(entry.pose(), 0, 0, 0)
            .color(1f, 0f, 0f, 1f)
            .normal(entry.normal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .endVertex();
        vertices
            .vertex(entry.pose(), (float)axis.x, (float)axis.y, (float)axis.z)
            .color(1f, 0f, 0f, 1f)
            .normal(entry.normal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .endVertex();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull ExperimentalPortal entity) {
        if (PortalCubedConfig.enableRoundPortals) {
            return !renderingTracers ? ROUND_TEXTURE : ROUND_TEXTURE_TRACER;
        } else {
            return !renderingTracers ? SQUARE_TEXTURE : SQUARE_TEXTURE_TRACER;
        }
    }
}
