package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Shadow @Final private Options options;

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;isDown()Z"
        )
    )
    private boolean ctrlToCrouch(KeyMapping instance, Operation<Boolean> original) {
        if (instance != options.keyShift || !PortalCubedClient.isPortalHudMode()) {
            return original.call(instance);
        }
        return original.call(instance) || options.keySprint.isDown();
    }
}
