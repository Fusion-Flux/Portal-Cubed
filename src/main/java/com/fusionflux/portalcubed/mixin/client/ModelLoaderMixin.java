package com.fusionflux.portalcubed.mixin.client;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.block.EmissiveModelRegistry;
import com.google.common.collect.Sets;

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

    @Unique
    private Set<Identifier> wrappedModels = Sets.newHashSet();

    @Inject(method = "bake", at = @At("RETURN"), cancellable = true)
    private void portalcubed$injectEmissiveModels(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
        if (wrappedModels.contains(id) || !id.getNamespace().equals(PortalCubed.MODID)) return;

        final BakedModel modelToWrap = cir.getReturnValue();
        final BakedModel emissiveModel = EmissiveModelRegistry.wrapModel(id, modelToWrap).orElseGet(() -> modelToWrap);
        if (emissiveModel == null || emissiveModel == modelToWrap) return;

        final Triple<Identifier, AffineTransformation, Boolean> triple = Triple.of(id, settings.getRotation(), settings.isUvLocked());
        bakedModelCache.put(triple, emissiveModel);
        cir.setReturnValue(emissiveModel);
    }

}
