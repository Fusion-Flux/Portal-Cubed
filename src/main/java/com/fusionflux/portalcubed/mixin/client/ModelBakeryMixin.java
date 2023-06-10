package com.fusionflux.portalcubed.mixin.client;

import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelBakery.class)
public interface ModelBakeryMixin {
    // TODO: Finish this mixin (may require AWs)
//    @Accessor
//    Map<ModelBakery.BakedCacheKey, BakedModel> getBakedCache();

    @Mixin(targets = "net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl")
    class ModelBakerImplMixin {
//        @Shadow private ModelBakery this$0;
//
//        @Inject(method = "bake", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//        private void portalcubed$injectEmissiveModels(ResourceLocation id, ModelState settings, CallbackInfoReturnable<BakedModel> cir, Triple<ResourceLocation, Transformation, Boolean> triple) {
//            if (this$0b.akedCache.get(triple) instanceof EmissiveBakedModel || !id.getNamespace().equals(PortalCubed.MOD_ID)) return;
//
//            final BakedModel modelToWrap = cir.getReturnValue();
//            final BakedModel customModel = EmissiveBakedModel.wrap(id, modelToWrap).orElse(modelToWrap);
//            if (customModel == null || customModel == modelToWrap) return;
//
//            bakedCache.replace(triple, customModel);
//            cir.setReturnValue(customModel);
//        }
    }

}
