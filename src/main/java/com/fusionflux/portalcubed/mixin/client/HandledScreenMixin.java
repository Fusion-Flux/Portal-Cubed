package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.FloorButtonBlock;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method = "closeScreen", at = @At("HEAD"))
    private void floorButtonEasterEgg(CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof AbstractInventoryScreen<?>) {
            FloorButtonBlock.enableEasterEgg = false;
        }
    }
}
