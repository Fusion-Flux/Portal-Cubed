package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(BlockHitResult.class)
public abstract class BlockHitResultMixin implements AdvancedRaycastResultHolder {
	@Unique
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
