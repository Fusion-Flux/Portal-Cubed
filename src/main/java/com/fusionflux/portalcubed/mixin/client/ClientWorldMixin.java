package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Mutable
    @Shadow @Final private static Set<Item> MARKER_PARTICLE_ITEMS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void powerParticle(CallbackInfo ci) {
        final Set<Item> newParticles = new HashSet<>(MARKER_PARTICLE_ITEMS);
        newParticles.add(PortalCubedBlocks.POWER_BLOCK.asItem());
        MARKER_PARTICLE_ITEMS = Set.copyOf(newParticles);
    }
}
