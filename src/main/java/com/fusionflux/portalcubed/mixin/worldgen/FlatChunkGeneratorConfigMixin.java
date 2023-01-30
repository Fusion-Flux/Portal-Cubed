package com.fusionflux.portalcubed.mixin.worldgen;

import com.fusionflux.portalcubed.worldgen.PortalCubedPlacedFeatures;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlatChunkGeneratorConfig.class)
public class FlatChunkGeneratorConfigMixin {
    @Inject(
        method = "method_44225",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/GenerationSettings$Builder;feature(Lnet/minecraft/world/gen/GenerationStep$Feature;Lnet/minecraft/util/Holder;)Lnet/minecraft/world/biome/GenerationSettings$Builder;",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    @SuppressWarnings("InvalidInjectorMethodSignature") // @Local isn't supported by mcdev
    private void gooLakes(Holder<Biome> holder, CallbackInfoReturnable<GenerationSettings> cir, @Local GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.LAKES, PortalCubedPlacedFeatures.LAKE_GOO_UNDERGROUND);
    }
}
