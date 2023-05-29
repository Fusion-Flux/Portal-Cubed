package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.accessor.FramebufferExt;
import com.fusionflux.portalcubed.client.render.entity.model.ExperimentalPortalModel;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.fusionflux.portalcubed.PortalCubed.id;
import static org.lwjgl.opengl.GL11.*;

public class ExperimentalPortalRenderer extends EntityRenderer<ExperimentalPortal> {
    private static final int MAX_PORTAL_LAYER = 0; // Change to 1 to work on rendering // No recursive rendering yet
    private static int portalLayer = 0;

    private static final Identifier SQUARE_TEXTURE = id("textures/entity/portal_square_outline_closed.png");
    private static final Identifier ROUND_TEXTURE  = id("textures/entity/portal_oval_outline_closed.png");
    private static final Identifier SQUARE_TEXTURE_TRACER = id("textures/entity/portal_tracer_square.png");
    private static final Identifier ROUND_TEXTURE_TRACER = id("textures/entity/portal_tracer_oval.png");
    protected final ExperimentalPortalModel model = new ExperimentalPortalModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ExperimentalPortalModel.MAIN_LAYER));

    public static boolean renderingTracers = false;

    public ExperimentalPortalRenderer(EntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ExperimentalPortal entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(entity.getYaw()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getPitch()));
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(entity.getRoll()));

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

        final float progress = (entity.age + tickDelta) / 2.5f;
        if (progress <= 1) {
            matrices.scale(progress, progress, progress);
        }

        renderPortal(matrices, vertexConsumers, entity, light, r, g, b, tickDelta);

        matrices.pop();

        if (MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes() && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            renderAxes(entity, matrices, vertexConsumers.getBuffer(RenderLayer.getLines()));
        }
    }

    private void renderPortal(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        ExperimentalPortal entity,
        int light,
        int r,
        int g,
        int b,
        float tickDelta
    ) {
        final boolean renderPortal = portalLayer < MAX_PORTAL_LAYER && entity.getActive();
        if (renderPortal) {
            // TODO: PortingLib compat
            ((FramebufferExt)MinecraftClient.getInstance().getFramebuffer()).setStencilBufferEnabled(true);
            glEnable(GL_STENCIL_TEST);
            RenderSystem.colorMask(false, false, false, false);
            RenderSystem.depthMask(false);
            RenderSystem.stencilMask(0xff);
            RenderSystem.stencilFunc(GL_GREATER, 0, 0xff);
            RenderSystem.stencilOp(GL_INCR, GL_KEEP, GL_KEEP);
            RenderSystem.clear(GL_STENCIL_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        }
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, r, g, b, 1F);
        if (renderPortal) {
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.depthMask(true);
            RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
            RenderSystem.stencilFunc(GL_NEVER, 123, 0xff);

            portalLayer++;
            final MinecraftClient minecraft = MinecraftClient.getInstance();
            final Camera camera = new Camera();
            ((CameraExt)camera).updateSimple(entity.world, entity);
            minecraft.worldRenderer.render(
                new MatrixStack(),
                tickDelta,
                0,
                false,
                camera,
                minecraft.gameRenderer,
                minecraft.gameRenderer.getLightmapTextureManager(),
                matrices.peek().getModel()
            );
            portalLayer--;

            glDisable(GL_STENCIL_TEST);
        }
    }

    private void renderAxes(ExperimentalPortal entity, MatrixStack matrices, VertexConsumer vertices) {
        final MatrixStack.Entry entry = matrices.peek();
        renderAxis(entry, vertices, entity.getNormal());
//        entity.getAxisW().ifPresent(axisW -> renderAxis(entry, vertices, axisW));
//        entity.getAxisH().ifPresent(axisH -> renderAxis(entry, vertices, axisH));
    }

    private void renderAxis(MatrixStack.Entry entry, VertexConsumer vertices, Vec3d axis) {
        vertices
            .vertex(entry.getModel(), 0, 0, 0)
            .color(1f, 0f, 0f, 1f)
            .normal(entry.getNormal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .next();
        vertices
            .vertex(entry.getModel(), (float)axis.x, (float)axis.y, (float)axis.z)
            .color(1f, 0f, 0f, 1f)
            .normal(entry.getNormal(), (float)axis.x, (float)axis.y, (float)axis.z)
            .next();
    }

    @Override
    public Identifier getTexture(ExperimentalPortal entity) {
        if (PortalCubedConfig.enableRoundPortals) {
            return !renderingTracers ? ROUND_TEXTURE : ROUND_TEXTURE_TRACER;
        } else {
            return !renderingTracers ? SQUARE_TEXTURE : SQUARE_TEXTURE_TRACER;
        }
    }
}
