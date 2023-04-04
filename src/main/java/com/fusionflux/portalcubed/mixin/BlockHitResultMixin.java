package com.fusionflux.portalcubed.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;

import net.minecraft.util.hit.BlockHitResult;

@Mixin(BlockHitResult.class)
public abstract class BlockHitResultMixin implements AdvancedRaycastResultHolder {
    private Optional<AdvancedEntityRaycast.Result> result = Optional.empty();

    @Override
    public Optional<AdvancedEntityRaycast.Result> getResult() {
        return result;
    }

    @Override
    public void setResult(Optional<AdvancedEntityRaycast.Result> result) {
        this.result = result;
    }
}
