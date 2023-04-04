package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Inject(
        method = "onAdvancements",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/packet/s2c/play/AdvancementUpdateS2CPacket;shouldClearCurrent()Z",
            ordinal = 1
        )
    )
    private void addGlobalAdvancement(AdvancementUpdateS2CPacket packet, CallbackInfo ci, @Local Advancement advancement, @Local AdvancementProgress advancementProgress) {
        if (advancementProgress.isDone()) {
            PortalCubedClient.addGlobalAdvancement(advancement.getId());
        }
    }
}
