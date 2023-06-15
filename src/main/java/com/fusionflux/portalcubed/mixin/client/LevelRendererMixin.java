package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "prepareCullFrustum", at = @At("HEAD"))
    private void modifyCameraRotation(PoseStack poseStack, Vec3 cameraPos, Matrix4f projectionMatrix, CallbackInfo ci) {
        PortalCubedClient.interpCamera().ifPresent(q -> poseStack.mulPose(q.toQuaternionf()));
    }
}
