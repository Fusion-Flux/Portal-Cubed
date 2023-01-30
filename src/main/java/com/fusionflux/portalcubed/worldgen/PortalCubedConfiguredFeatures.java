package com.fusionflux.portalcubed.worldgen;

import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import net.minecraft.block.Blocks;
import net.minecraft.util.Holder;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.util.ConfiguredFeatureUtil;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class PortalCubedConfiguredFeatures {
    @SuppressWarnings("deprecation")
    public static final Holder<ConfiguredFeature<LakeFeature.Config, ?>> LAKE_GOO = ConfiguredFeatureUtil.register(
        "portalcubed:lake_goo",
        Feature.LAKE,
        new LakeFeature.Config(
            BlockStateProvider.of(PortalCubedFluids.TOXIC_GOO.getBlock().getDefaultState()),
            BlockStateProvider.of(Blocks.DEEPSLATE.getDefaultState())
        )
    );
}
