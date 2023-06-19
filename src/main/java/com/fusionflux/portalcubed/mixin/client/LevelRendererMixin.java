package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.LevelRendererExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.entity.PortalRenderer;
import com.fusionflux.portalcubed.client.render.portal.PortalRenderPhase;
import com.fusionflux.portalcubed.entity.Portal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements LevelRendererExt {
    @Shadow private Frustum cullingFrustum;

    @Mutable
    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow @Final private Minecraft minecraft;

    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "prepareCullFrustum", at = @At("HEAD"))
    private void modifyCameraRotation(PoseStack poseStack, Vec3 cameraPos, Matrix4f projectionMatrix, CallbackInfo ci) {
        PortalCubedClient.interpCamera().ifPresent(q -> poseStack.mulPose(q.toQuaternionf()));
        if (PortalCubedClient.cameraTransformedThroughPortal != null && !minecraft.gameRenderer.getMainCamera().isDetached()) {
            poseStack.mulPose(PortalCubedClient.cameraTransformedThroughPortal.getTransformQuat().toQuaternionf().conjugate());
        }
    }

    @Override
    public Frustum getCullingFrustum() {
        return cullingFrustum;
    }

    @Override
    public void setCullingFrustum(Frustum cullingFrustum) {
        this.cullingFrustum = cullingFrustum;
    }

    @Override
    public RenderBuffers getRenderBuffers() {
        return renderBuffers;
    }

    @Override
    public void setRenderBuffers(RenderBuffers renderBuffers) {
        this.renderBuffers = renderBuffers;
    }

    @WrapOperation(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;isDetached()Z"
        )
    )
    private boolean overrideDetached(Camera instance, Operation<Boolean> original) {
        return PortalCubedClient.cameraTransformedThroughPortal != null || original.call(instance);
    }

    @Inject(
        method = "renderLevel",
        at = @At("RETURN")
    )
    private void renderEndNoFapi(
        PoseStack poseStack,
        float partialTick,
        long finishNanoTime,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        assert level != null;

        if (PortalCubedClient.getRenderer().targetPhase() != PortalRenderPhase.FINAL) return;
        final MultiBufferSource.BufferSource consumers = renderBuffers.bufferSource();
        final var cameraPos = camera.getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        final PortalRenderPhase oldRenderPhase = PortalRenderer.renderPhase;
        PortalRenderer.renderPhase = PortalRenderPhase.FINAL;

        final EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        for (final Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof Portal portal) || !portal.getActive()) continue;
            poseStack.pushPose();
            poseStack.translate(entity.getX(), entity.getY(), entity.getZ());
            poseStack.mulPose(portal.getRotation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            ((PortalRenderer)dispatcher.getRenderer(portal)).renderPortal(
                poseStack,
                consumers,
                portal,
                0, 1, 1, 1,
                partialTick
            );
            poseStack.popPose();
        }

        poseStack.popPose();
        consumers.endLastBatch();
        PortalRenderer.renderPhase = oldRenderPhase;
    }
}
