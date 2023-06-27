package com.fusionflux.portalcubed.data;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.bridge.HardLightBridgePart;
import com.fusionflux.portalcubed.blocks.bridge.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelTubeBlock;
import com.fusionflux.portalcubed.blocks.funnel.TwoByTwoFacingMultiblockBlock;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class PortalCubedModels extends FabricModelProvider {
    public PortalCubedModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        gen.blockStateOutput.accept(excursionFunnelEmitter());
        gen.skipAutoItemBlock(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER);
        gen.blockStateOutput.accept(excursionFunnel());

        gen.skipAutoItemBlock(PortalCubedBlocks.HLB_EMITTER_BLOCK);
        gen.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(PortalCubedBlocks.HLB_EMITTER_BLOCK)
                        .with(BlockModelGenerators.createFacingDispatch())
                        .with(PropertyDispatch.properties(HardLightBridgePart.EDGE, HardLightBridgeEmitterBlock.POWERED)
                                .generate((edge, powered) -> {
                                    String power = powered ? "on" : "off";
                                    String model = "block/light_bridge_emitter_" + edge + "_edge_" + power;
                                    ResourceLocation id = PortalCubed.id(model);
                                    return Variant.variant().with(VariantProperties.MODEL, id);
                                })
                        )
        );
        gen.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(PortalCubedBlocks.HLB_BLOCK)
                        .with(BlockModelGenerators.createFacingDispatch())
                        .with(PropertyDispatch.property(HardLightBridgePart.EDGE)
                                .generate(edge -> {
                                    ResourceLocation id = PortalCubed.id("block/light_bridge_" + edge);
                                    return Variant.variant().with(VariantProperties.MODEL, id);
                                })
                        )
        );
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

    protected void inheritFrom(ItemLike item, ResourceLocation parent, ItemModelGenerators gen) {
        gen.output.accept(ModelLocationUtils.getModelLocation(item.asItem()), () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", parent.toString());
            return json;
        });
    }
}
