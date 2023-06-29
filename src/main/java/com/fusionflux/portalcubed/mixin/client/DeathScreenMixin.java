package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {
    @Shadow private int delayTicker;

    protected DeathScreenMixin(Component title) {
        super(title);
        throw new AssertionError();
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void noDeathScreenI(CallbackInfo ci) {
        assert minecraft != null;
        if (PortalCubedClient.isPortalHudMode()) {
            delayTicker = 0;
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void noDeathScreenR(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            assert minecraft != null;
            minecraft.mouseHandler.grabMouse();
            ci.cancel();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert minecraft != null;
        assert minecraft.player != null;
        if (PortalCubedClient.isPortalHudMode() && delayTicker >= (minecraft.player.shouldShowDeathScreen() ? 20 : 0)) {
            minecraft.player.respawn();
            minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
