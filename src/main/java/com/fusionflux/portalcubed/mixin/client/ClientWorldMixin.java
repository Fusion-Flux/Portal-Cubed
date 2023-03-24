package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.SpecialHiddenBlock;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @WrapOperation(
        method = "getMarkerParticleTarget",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
        ),
        require = 0
    )
    private static boolean specialHiddenBlocks(Set<Item> instance, Object item, Operation<Boolean> original) {
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SpecialHiddenBlock) {
            return !PortalCubedClient.hiddenBlocksVisible();
        }
        return original.call(instance, item);
    }
}
