package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRH(float tickDelta, PoseStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRSB(PoseStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMH(PoseStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMJB(PoseStack matrices, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudREB(PoseStack matrices, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }
}
