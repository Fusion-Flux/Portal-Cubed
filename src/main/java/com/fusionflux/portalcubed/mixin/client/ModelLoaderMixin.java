package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.block.EmissiveBakedModel;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelLoaderMixin {

    @Shadow
    @Final
    private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Inject(method = "bake", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void portalcubed$injectEmissiveModels(ResourceLocation id, ModelState settings, CallbackInfoReturnable<BakedModel> cir, Triple<ResourceLocation, Transformation, Boolean> triple) {
        if (bakedCache.get(triple) instanceof EmissiveBakedModel || !id.getNamespace().equals(PortalCubed.MOD_ID)) return;

        final BakedModel modelToWrap = cir.getReturnValue();
        final BakedModel customModel = EmissiveBakedModel.wrap(id, modelToWrap).orElse(modelToWrap);
        if (customModel == null || customModel == modelToWrap) return;

        bakedCache.replace(triple, customModel);
        cir.setReturnValue(customModel);
    }

}
