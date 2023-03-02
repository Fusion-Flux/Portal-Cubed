package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapWithCondition(
        method = "lockCursor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
        )
    )
    private boolean notPortalHud(MinecraftClient client, Screen screen) {
        return screen != null || !(client.currentScreen instanceof DeathScreen) || !PortalCubedClient.isPortalHudMode();
    }

    @WrapOperation(
        method = "updateLookDirection",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/Option;get()Ljava/lang/Object;"
        )
    )
    private Object slowLook(Option<Double> instance, Operation<Double> original) {
        if (instance == client.options.getMouseSensitivity() && PortalCubedClient.zoomDir > 0) {
            return original.call(instance) / 2;
        }
        return original.call(instance);
    }
}
