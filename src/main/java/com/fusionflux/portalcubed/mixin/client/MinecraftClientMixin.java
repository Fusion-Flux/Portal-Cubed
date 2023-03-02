package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.items.PaintGun;
import com.fusionflux.portalcubed.items.PortalGun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;
    @Shadow public ClientPlayerEntity player;
    @Shadow private void doItemUse() {
        throw new UnsupportedOperationException();
    }
    @Shadow private boolean doAttack() {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 1))
    private void portalCubed$stopPortalSpamming(MinecraftClient self) {
        if (!(player.getMainHandStack().getItem() instanceof PortalGun)) {
            doItemUse();
        }
    }

    @Inject(method = "handleInputEvents", at = @At("RETURN"))
    private void portalCubed$allowConstantAttack(CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof PaintGun && options.attackKey.isPressed()) {
            doAttack();
        }
    }

}
