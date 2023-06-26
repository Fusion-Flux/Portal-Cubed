package com.fusionflux.portalcubed.data;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelTubeBlock;
import com.fusionflux.portalcubed.blocks.funnel.TwoByTwoFacingMultiblockBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;

public class PortalCubedModels extends FabricModelProvider {
    public PortalCubedModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        gen.blockStateOutput.accept(excursionFunnelEmitter());
        gen.skipAutoItemBlock(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER);
        gen.blockStateOutput.accept(excursionFunnel());
    }

    protected BlockStateGenerator excursionFunnelEmitter() {
        return MultiVariantGenerator.multiVariant(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER)
                .with(
                        PropertyDispatch.properties(ExcursionFunnelEmitterBlock.QUADRANT, ExcursionFunnelEmitterBlock.MODE)
                        .generate(this::generateEmitterVariant)
                )
                .with(BlockModelGenerators.createFacingDispatch());
    }

    protected Variant generateEmitterVariant(int quadrant, Mode state) {
        ResourceLocation id = PortalCubed.id("block/excursion_funnel_emitter_" + state.getSerializedName() + "_quadrant_" + quadrant);
        return Variant.variant().with(VariantProperties.MODEL, id);
    }

    protected BlockStateGenerator excursionFunnel() {
        return MultiVariantGenerator.multiVariant(PortalCubedBlocks.EXCURSION_FUNNEL)
                .with(
                        PropertyDispatch.properties(TwoByTwoFacingMultiblockBlock.QUADRANT, ExcursionFunnelTubeBlock.REVERSED)
                                .generate(this::generateTubeVariant)
                )
                .with(BlockModelGenerators.createFacingDispatch());
    }

    protected Variant generateTubeVariant(int quadrant, boolean reversed) {
        String reversion = reversed ? "reversed_" : "";
        ResourceLocation id = PortalCubed.id("block/excursion_funnel_" + reversion + "quadrant_" + quadrant);
        return Variant.variant().with(VariantProperties.MODEL, id);
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
    }
}
