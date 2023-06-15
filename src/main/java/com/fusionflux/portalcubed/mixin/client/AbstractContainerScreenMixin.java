package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.FloorButtonBlock;
import com.fusionflux.portalcubed.mixin.CreativeModeTabsAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(method = "onClose", at = @At("HEAD"))
    private void floorButtonEasterEgg(CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof EffectRenderingInventoryScreen<?>) {
            if (FloorButtonBlock.enableEasterEgg) {
                FloorButtonBlock.enableEasterEgg = false;
                CreativeModeTabsAccessor.setCACHED_PARAMETERS(null);
            }
        }
    }
}
