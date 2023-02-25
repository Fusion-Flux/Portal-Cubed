package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {
    @Shadow private int ticksSinceDeath;

    protected DeathScreenMixin(Text title) {
        super(title);
        throw new AssertionError();
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void noDeathScreenI(CallbackInfo ci) {
        assert client != null;
        if (PortalCubedClient.isPortalHudMode()) {
            ticksSinceDeath = 0;
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void noDeathScreenR(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            assert client != null;
            client.mouse.lockCursor();
            ci.cancel();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert client != null;
        assert client.player != null;
        if (PortalCubedClient.isPortalHudMode() && ticksSinceDeath >= (client.player.showsDeathScreen() ? 20 : 0)) {
            client.player.requestRespawn();
            client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
