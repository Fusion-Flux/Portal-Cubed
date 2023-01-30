package com.fusionflux.portalcubed.worldgen;

import net.minecraft.util.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureUtil;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class PortalCubedPlacedFeatures {
    public static final Holder<PlacedFeature> LAKE_GOO_UNDERGROUND = PlacedFeatureUtil.register(
        "portalcubed:lake_goo_underground",
        PortalCubedConfiguredFeatures.LAKE_GOO,
        RarityFilterPlacementModifier.create(9),
        InSquarePlacementModifier.getInstance(),
        HeightRangePlacementModifier.create(UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(0))),
        EnvironmentScanPlacementModifier.create(
            Direction.DOWN,
            BlockPredicate.bothOf(
                BlockPredicate.not(BlockPredicate.IS_AIR),
                BlockPredicate.insideWorldBounds(new BlockPos(0, -5, 0))
            ),
            32
        ),
        SurfaceRelativeThresholdFilterPlacementModifier.create(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
        BiomePlacementModifier.getInstance()
    );
}
