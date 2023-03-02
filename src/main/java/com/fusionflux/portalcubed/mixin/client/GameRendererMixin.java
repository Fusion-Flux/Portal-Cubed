package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(
        method = {"getFov", "bobViewWhenHurt"},
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(FF)F"
        )
    )
    private float noDeathEffects(float a, float b, Operation<Float> original) {
        if (PortalCubedClient.isPortalHudMode()) {
            return 0;
        }
        return original.call(a, b);
    }
}
