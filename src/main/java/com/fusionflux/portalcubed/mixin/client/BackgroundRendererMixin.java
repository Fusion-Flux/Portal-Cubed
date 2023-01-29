package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.fluids.ToxicGooFluid;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
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
    private static void portalcubed$renderCustomFluidFog(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

        if (cameraSubmergedFluidState.getFluid() instanceof ToxicGooFluid) {
            // Dark brownish "red"
            red = 99 / 255f;
            green = 29 / 255f;
            blue = 1 / 255f;

            lastWaterFogColorUpdateTime = -1L;

            RenderSystem.clearColor(red, green, blue, 0.0F);

            ci.cancel();
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        FluidState cameraSubmergedFluidState = ((CameraExt) camera).portalcubed$getSubmergedFluidState();

        if (cameraSubmergedFluidState.getFluid() instanceof ToxicGooFluid) {
            // Same fog as lava with fire resistance
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(3.0F);

            ci.cancel();
        }
    }
}
