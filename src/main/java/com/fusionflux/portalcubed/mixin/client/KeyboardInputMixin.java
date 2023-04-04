package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Shadow @Final private GameOptions options;

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBind;isPressed()Z"
        )
    )
    private boolean ctrlToCrouch(KeyBind instance, Operation<Boolean> original) {
        if (instance != options.sneakKey || !PortalCubedClient.isPortalHudMode()) {
            return original.call(instance);
        }
        return original.call(instance) || options.sprintKey.isPressed();
    }
}
