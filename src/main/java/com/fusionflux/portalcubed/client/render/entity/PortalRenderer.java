package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.entity.model.PortalModel;
import com.fusionflux.portalcubed.client.render.portal.PortalRenderPhase;
import com.fusionflux.portalcubed.client.render.portal.PortalRendererImpl;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.util.GeneralUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
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
import org.joml.Quaternionf;

import java.util.List;
import java.util.UUID;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalRenderer extends EntityRenderer<Portal> {
    private static final ResourceLocation SQUARE_TEXTURE = id("textures/entity/portal_square_outline_closed.png");
    private static final ResourceLocation ROUND_TEXTURE  = id("textures/entity/portal_oval_outline_closed.png");
    private static final ResourceLocation SQUARE_TEXTURE_TRACER = id("textures/entity/portal_tracer_square.png");
    private static final ResourceLocation ROUND_TEXTURE_TRACER = id("textures/entity/portal_tracer_oval.png");
    protected final PortalModel model = new PortalModel(Minecraft.getInstance().getEntityModels().bakeLayer(PortalModel.MAIN_LAYER));

    public static PortalRenderPhase renderPhase = PortalRenderPhase.ENTITY;

    public PortalRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@NotNull Portal entity, float yaw, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pushPose();
        Quaternionf rotation = entity.getRotation().get(tickDelta);
        matrices.mulPose(rotation);
        matrices.mulPose(Axis.YP.rotationDegrees(180));

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
            LevelRenderer.renderLineBox(
                matrices,
                vertexConsumers.getBuffer(RenderType.lines()),
                GeneralUtil.capAABBAt(
                    entity.getOriginPos().subtract(2, 2, 2),
                    entity.getOriginPos().add(2, 2, 2),
                    entity.getFacingDirection(),
                    entity.getOriginPos()
                ).move(-entity.getX(), -entity.getY(), -entity.getZ()),
                1f, 1f, 0f, 1f
            );
            LevelRenderer.renderVoxelShape(
                matrices,
                vertexConsumers.getBuffer(RenderType.lines()),
                entity.getCrossPortalCollisionShapeOther(entity),
                -entity.getX(), -entity.getY(), -entity.getZ(),
                1f, 0.55f, 0f, 1f,
                true
            );
        }
    }

    public void renderPortal(
        PoseStack poseStack,
        MultiBufferSource vertexConsumers,
        Portal entity,
        int light,
        int r,
        int g,
        int b,
        float tickDelta
    ) {
        final PortalRendererImpl renderer = PortalCubedClient.getRenderer();
        final boolean renderContents = renderPhase == renderer.targetPhase() && renderer.enabled(entity);
        if (renderContents) {
            renderer.preRender(entity, tickDelta, poseStack, vertexConsumers);
        }
        model.renderToBuffer(poseStack, vertexConsumers.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY, r, g, b, 1F);
        if (renderContents) {
            renderer.postRender(entity, tickDelta, poseStack, vertexConsumers);
        }
    }

    private void renderOtherEntities(Portal entity, PoseStack poseStack, float tickDelta, MultiBufferSource buffer, int packedLight) {
        if (renderPhase != PortalRenderPhase.ENTITY || !entity.getActive()) return;
        final UUID otherUuid = entity.getLinkedPortalUUID().orElse(null);
        if (otherUuid == null || !(((LevelExt)entity.level()).getEntityByUuid(otherUuid) instanceof Portal otherPortal)) return;
        final double oplx = Mth.lerp(tickDelta, otherPortal.xOld, otherPortal.getX());
        final double oply = Mth.lerp(tickDelta, otherPortal.yOld, otherPortal.getY());
        final double oplz = Mth.lerp(tickDelta, otherPortal.zOld, otherPortal.getZ());
        final List<Entity> otherEntities = otherPortal.level().getEntities(otherPortal, otherPortal.getBoundingBox().deflate(0.01), e -> !e.isInvisible());
        final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        final boolean renderHitboxes = dispatcher.shouldRenderHitBoxes();
        dispatcher.setRenderHitBoxes(false);
        for (final Entity otherEntity : otherEntities) {
            if (otherEntity instanceof Portal) continue;
            if (PortalCubedClient.cameraTransformedThroughPortal != null) {
                final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                if (otherEntity == camera.getEntity() && !camera.isDetached()) continue;
            }
            poseStack.pushPose();
            poseStack.mulPose(otherPortal.getTransformQuat().toQuaternionf());
            poseStack.translate(0, Portal.SURFACE_OFFSET, 0);
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

    private void renderAxes(Portal entity, PoseStack matrices, VertexConsumer vertices) {
        final PoseStack.Pose entry = matrices.last();
        renderAxis(entry, vertices, entity.getAxisW(), 1f, 0f, 0f);
        renderAxis(entry, vertices, entity.getAxisH(), 1f, 0f, 0f);
        renderAxis(entry, vertices, entity.getNormal(), 0f, 1f, 0f);
        Quaternionf rotation = entity.getRotation().get();
        Vec3 rotationAxis = new Vec3(rotation.x, rotation.y, rotation.z).normalize();
        renderAxis(entry, vertices, rotationAxis, 0.5f, 0f, 0.5f);
    }

    private void renderAxis(PoseStack.Pose entry, VertexConsumer vertices, Vec3 axis, float red, float green, float blue) {
        vertices
            .vertex(entry.pose(), 0, 0, 0)
            .color(red, green, blue, 1f)
            .normal(entry.normal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .endVertex();
        vertices
            .vertex(entry.pose(), (float)axis.x, (float)axis.y, (float)axis.z)
            .color(red, green, blue, 1f)
            .normal(entry.normal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .endVertex();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull Portal entity) {
        if (PortalCubedConfig.enableRoundPortals) {
            return renderPhase == PortalRenderPhase.TRACER ? ROUND_TEXTURE_TRACER : ROUND_TEXTURE;
        } else {
            return renderPhase == PortalRenderPhase.TRACER ? SQUARE_TEXTURE_TRACER : SQUARE_TEXTURE;
        }
    }
}
