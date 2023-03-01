package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRH(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRSB(MatrixStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMH(MatrixStack matrices, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMJB(MatrixStack matrices, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudREB(MatrixStack matrices, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }
}
