package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementMixin {
    @Inject(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/game/ClientboundUpdateAdvancementsPacket;shouldReset()Z",
            ordinal = 1
        )
    )
    private void addGlobalAdvancement(ClientboundUpdateAdvancementsPacket packet, CallbackInfo ci, @Local Advancement advancement, @Local AdvancementProgress advancementProgress) {
        if (advancementProgress.isDone()) {
            PortalCubedClient.addGlobalAdvancement(advancement.getId());
        }
    }
}
