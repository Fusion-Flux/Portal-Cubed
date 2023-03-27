package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.fluids.ToxicGooFluid;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.mojang.blaze3d.shader.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;
    @Shadow
    private static long lastWaterFogColorUpdateTime;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void renderToxicGooFog(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

        if (cameraSubmergedFluidState.getFluid() instanceof ToxicGooFluid) {
            ci.cancel();

            // Dark brownish "red"
            red = 99 / 255f;
            green = 29 / 255f;
            blue = 1 / 255f;

            lastWaterFogColorUpdateTime = -1L;

            // Dark brownish "red"
            RenderSystem.clearColor(red, green, blue, 0f);
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;pow(DD)D"
        ),
        cancellable = true
    )
    private static void renderPortalFog(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        final FogSettings fog = PortalCubedClient.customFog;
        if (fog != null) {
            ci.cancel();

            red = fog.color().r() / 255f;
            green = fog.color().g() / 255f;
            blue = fog.color().b() / 255f;

            RenderSystem.clearColor(red, green, blue, 0f);
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void applyToxicGooFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

        if (cameraSubmergedFluidState.getFluid() instanceof ToxicGooFluid) {
            ci.cancel();

            if (camera.getFocusedEntity().isSpectator()) {
                RenderSystem.setShaderFogStart(-8.0F);
                RenderSystem.setShaderFogEnd(viewDistance * 0.5F);
            } else {
                // Same fog as lava with fire resistance
                RenderSystem.setShaderFogStart(0.0F);
                RenderSystem.setShaderFogEnd(3.0F);
            }
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }

    @Inject(
        method = "applyFog",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/BackgroundRenderer$FogType;FOG_SKY:Lnet/minecraft/client/render/BackgroundRenderer$FogType;",
            opcode = Opcodes.GETSTATIC
        ),
        cancellable = true
    )
    private static void applyPortalFog1(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        applyPortalFog0(ci);
    }

    @Inject(
        method = "applyFog",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(FF)F"
        ),
        cancellable = true
    )
    private static void applyPortalFog2(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        applyPortalFog0(ci);
    }

    private static void applyPortalFog0(CallbackInfo ci) {
        final FogSettings fog = PortalCubedClient.customFog;
        if (fog != null) {
            ci.cancel();

            RenderSystem.setShaderFogStart(fog.start());
            RenderSystem.setShaderFogEnd(fog.end());
            RenderSystem.setShaderFogShape(fog.shape().toBlaze3d());
        }
    }
}
