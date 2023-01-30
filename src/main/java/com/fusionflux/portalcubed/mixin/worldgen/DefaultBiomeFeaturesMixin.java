package com.fusionflux.portalcubed.mixin.worldgen;

import com.fusionflux.portalcubed.worldgen.PortalCubedPlacedFeatures;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
    @Inject(method = "addLandCarvers", at = @At("TAIL"))
    private static void gooLakes(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.feature(GenerationStep.Feature.LAKES, PortalCubedPlacedFeatures.LAKE_GOO_UNDERGROUND);
    }
}
