package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;"
        )
    )
    private static Set<Item> addMyStuff(Set<Item> original) {
        final Set<Item> newSet = new HashSet<>(original);
        newSet.add(PortalCubedBlocks.POWER_BLOCK.asItem());
        return newSet;
    }
}
