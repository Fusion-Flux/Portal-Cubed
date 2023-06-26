package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.blocks.BucketPickupEx;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @WrapOperation(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/BucketPickup;getPickupSound()Ljava/util/Optional;"
        )
    )
    private Optional<SoundEvent> pickupSoundWithFluid(BucketPickup instance, Operation<Optional<SoundEvent>> original, @Local BlockState blockState) {
        return instance instanceof BucketPickupEx ex ? ex.getPickupSound(blockState) : original.call(instance);
    }
}
