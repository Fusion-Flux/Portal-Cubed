package com.fusionflux.portalcubed.mixin.client;

import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.block.EmissiveBakedModel;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

	@Shadow
	@Final
	private Map<Triple<Identifier, AffineTransformation, Boolean>, BakedModel> bakedModelCache;

	@Inject(method = "bake", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void portalcubed$injectEmissiveModels(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir, Triple<Identifier, AffineTransformation, Boolean> triple) {
		if (bakedModelCache.get(triple) instanceof EmissiveBakedModel || !id.getNamespace().equals(PortalCubed.MODID)) return;

		final BakedModel modelToWrap = cir.getReturnValue();
		final BakedModel customModel = EmissiveBakedModel.wrap(id, modelToWrap).orElseGet(() -> modelToWrap);
		if (customModel == null || customModel == modelToWrap) return;

		bakedModelCache.replace(triple, customModel);
		cir.setReturnValue(customModel);
	}

}
