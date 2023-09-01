package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.block.EmissiveBakedModel;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("checkstyle:TypeName")
@Mixin(targets = "net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl")
public class ModelBakery_ModelBakerImplMixin {
	@Final
	@Shadow
	@SuppressWarnings("checkstyle:MemberName")
	ModelBakery field_40571;

	@Inject(method = "bake", at = @At("RETURN"), cancellable = true)
	private void portalcubed$injectEmissiveModels(
		ResourceLocation location,
		ModelState transform,
		CallbackInfoReturnable<BakedModel> cir,
		@Local ModelBakery.BakedCacheKey bakedCacheKey
	) {
		if (field_40571.bakedCache.get(bakedCacheKey) instanceof EmissiveBakedModel || !location.getNamespace().equals(PortalCubed.MOD_ID))
			return;

		final BakedModel modelToWrap = cir.getReturnValue();
		final BakedModel customModel = EmissiveBakedModel.wrap(location, modelToWrap).orElse(modelToWrap);
		if (customModel == null || customModel == modelToWrap) return;

		field_40571.bakedCache.replace(bakedCacheKey, customModel);
		cir.setReturnValue(customModel);
	}
}
