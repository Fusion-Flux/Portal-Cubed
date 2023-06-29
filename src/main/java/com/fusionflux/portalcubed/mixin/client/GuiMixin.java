package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRH(float tickDelta, GuiGraphics graphics, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRSB(GuiGraphics graphics, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMH(GuiGraphics graphics, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudRMJB(PlayerRideableJumping rideable, GuiGraphics graphics, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void notWithPortalHudREB(GuiGraphics graphics, int x, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }
}
