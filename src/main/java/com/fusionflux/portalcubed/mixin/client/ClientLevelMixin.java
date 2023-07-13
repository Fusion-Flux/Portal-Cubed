package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.SpecialHiddenBlock;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.LateRenderedEntitySortingIterator;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
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

    @ModifyReturnValue(method = "entitiesForRendering", at = @At("TAIL"))
    private Iterable<Entity> wrapRenderedEntityIterator(Iterable<Entity> entities) {
        return () -> new LateRenderedEntitySortingIterator(entities.iterator());
    }
}
