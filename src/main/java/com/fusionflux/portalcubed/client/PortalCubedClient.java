package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.key.GrabKeyBinding;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.*;
import com.fusionflux.portalcubed.client.render.model.block.EmissiveModelRegistry;
import com.fusionflux.portalcubed.client.render.model.entity.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.FaithPlateScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.DyeableItem;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import static com.fusionflux.portalcubed.PortalCubed.id;

@Environment(EnvType.CLIENT)
public class PortalCubedClient implements ClientModInitializer {
    public static long shakeStart;

    @Override
    public void onInitializeClient(ModContainer mod) {

        ScreenRegistry.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);

        registerEntityRenderers();
        registerColorProviders();
        setRenderLayers();
        registerEmissiveModels();
        PortalCubedClientPackets.registerPackets();
        GrabKeyBinding.register();

        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);

        final Identifier toxicGooStillSpriteId = id("block/toxic_goo_still");
        final Identifier toxicGooFlowSpriteId = id("block/toxic_goo_flow");
        FluidRenderHandlerRegistry.INSTANCE.register(PortalCubedFluids.TOXIC_GOO.still, PortalCubedFluids.TOXIC_GOO.flowing, new SimpleFluidRenderHandler(toxicGooStillSpriteId, toxicGooFlowSpriteId));

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(toxicGooStillSpriteId);
            registry.register(toxicGooFlowSpriteId);
        });

        BlockRenderLayerMap.put(
            RenderLayer.getTranslucent(),
            PortalCubedBlocks.ABSOLUTE_FIZZLER,
            PortalCubedBlocks.DEATH_FIZZLER,
            PortalCubedBlocks.LASER_FIZZLER
        );
    }

    private void setRenderLayers() {
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.HLB_BLOCK);
        BlockRenderLayerMap.put(RenderLayer.getCutout(), PortalCubedBlocks.HLB_EMITTER_BLOCK);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.NEUROTOXIN_BLOCK);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.EXCURSION_FUNNEL);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.DUEL_EXCURSION_FUNNEL_EMITTER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_EMITTER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.NEUROTOXIN_EMITTER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.LASER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.LASER_EMITTER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.LASER_CATCHER);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.LASER_RELAY);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.CONVERSION_GEL);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.PROPULSION_GEL);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.REPULSION_GEL);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.ADHESION_GEL);

        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.BETA_FAITH_PLATE);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.FAITH_PLATE);

        BlockRenderLayerMap.put(RenderLayer.getCutout(), PortalCubedBlocks.PORTAL1DOOR);
        BlockRenderLayerMap.put(RenderLayer.getCutout(), PortalCubedBlocks.PORTAL2DOOR);
        BlockRenderLayerMap.put(RenderLayer.getCutout(), PortalCubedBlocks.OLDAPDOOR);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._1x1_DOUBLE_CROSSBAR);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._1x1_SINGLE_CROSSBAR);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._2X2_DOUBLE_CROSSBAR_BOTTOM_LEFT);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._2X2_DOUBLE_CROSSBAR_BOTTOM_RIGHT);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._2X2_DOUBLE_CROSSBAR_TOP_LEFT);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks._2X2_DOUBLE_CROSSBAR_TOP_RIGHT);
    }

    private void registerEmissiveModels() {
        // Misc
        EmissiveModelRegistry.register(id("block/excursion_funnel"), id("block/excursion_funnel_beam"));
        EmissiveModelRegistry.register(id("block/excursion_funnel_reversed"), id("block/reverse_excursion_funnel_beam"));

        EmissiveModelRegistry.register(id("block/faith_plate"), id("block/faith_plate_e"));
        EmissiveModelRegistry.register(id("block/faith_plate_active"), id("block/faith_plate_e"));

        EmissiveModelRegistry.register(id("block/laser"), id("block/laser_beam"));
        EmissiveModelRegistry.register(id("block/laser_ref_main"), id("block/laser_beam"));
        EmissiveModelRegistry.register(id("block/laser_ref_prev"), id("block/laser_beam"));

        EmissiveModelRegistry.register(id("block/portal_2_laser_field_top"), id("block/portal_2_laser_field_top"));
        EmissiveModelRegistry.register(id("block/portal_2_laser_field_bottom"), id("block/portal_2_laser_field_bottom"));

        EmissiveModelRegistry.register(id("block/portal_2_fizzler_top"), id("block/portal_2_fizzler_top"));
        EmissiveModelRegistry.register(id("block/portal_2_fizzler_bottom"), id("block/portal_2_fizzler_bottom"));

        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_top"), id("block/portal_2_death_fizzler_top"));
        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_bottom"), id("block/portal_2_death_fizzler_bottom"));


        EmissiveModelRegistry.register(id("block/portal_2_fizzler_emitter_top"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_fizzler_emitter_bottom"), id("block/grill_emitters_e"));

        EmissiveModelRegistry.register(id("block/portal_2_laser_field_emitter_top"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_laser_field_emitter_bottom"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_laser_field_emitter_top_disabled"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_laser_field_emitter_bottom_disabled"), id("block/grill_emitters_e"));

        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_emitter_top"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_emitter_bottom"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_emitter_top_disabled"), id("block/grill_emitters_e"));
        EmissiveModelRegistry.register(id("block/portal_2_death_fizzler_emitter_bottom_disabled"), id("block/grill_emitters_e"));

        EmissiveModelRegistry.register(id("block/light_bridge"), id("block/light_bridge"));

        EmissiveModelRegistry.register(id("block/auto_portal_lower"), id("block/auto_portal_e"));
        EmissiveModelRegistry.register(id("block/auto_portal_upper"), id("block/auto_portal_e"));


        EmissiveModelRegistry.register(id("block/portal_1_door_bottom_left"), id("block/portal_1_door_e"));
        EmissiveModelRegistry.register(id("block/portal_1_door_bottom_left_open"), id("block/portal_1_door_e"));
        EmissiveModelRegistry.register(id("block/portal_1_door_bottom_right"), id("block/portal_1_door_e"));
        EmissiveModelRegistry.register(id("block/portal_1_door_bottom_right_open"), id("block/portal_1_door_e"));

        EmissiveModelRegistry.register(id("block/portal_2_door_bottom_right"), id("block/portal_2_door_e"));
        EmissiveModelRegistry.register(id("block/portal_2_door_bottom_right_open"), id("block/portal_2_door_e"));
        EmissiveModelRegistry.register(id("block/portal_2_door_top_right"), id("block/portal_2_door_e"));
        EmissiveModelRegistry.register(id("block/portal_2_door_top_right_open"), id("block/portal_2_door_e"));

        // Emitters

        EmissiveModelRegistry.register(id("block/light_bridge_emitter"), id("block/light_bridge_emitter_e"));

        EmissiveModelRegistry.register(id("block/excursion_funnel_emitter_off"), id("block/funnel_wings_e"));
        EmissiveModelRegistry.register(id("block/excursion_funnel_emitter"), id("block/funnel_wings_active_e"));
        EmissiveModelRegistry.register(id("block/excursion_funnel_emitter_reversed"), id("block/funnel_wings_active_reverse_e"));

        EmissiveModelRegistry.register(id("block/laser_emitter_active"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_emitter_active_downward"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_emitter_active_upward"), id("block/laser_emitter_e"));

        EmissiveModelRegistry.register(id("block/laser_relay_active"), id("block/laser_emitter_e"));

        EmissiveModelRegistry.register(id("block/laser_catcher_active"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_catcher_active_downward"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_catcher_active_upward"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_catcher"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_catcher_downward"), id("block/laser_emitter_e"));
        EmissiveModelRegistry.register(id("block/laser_catcher_upward"), id("block/laser_emitter_e"));

        // Buttons

        EmissiveModelRegistry.register(id("block/floor_button"), id("block/floor_button_e"));
        EmissiveModelRegistry.register(id("block/pedestal_button"), id("block/pedestal_button_e"));
        EmissiveModelRegistry.register(id("block/floor_button_active"), id("block/floor_button_e"));
        EmissiveModelRegistry.register(id("block/pedestal_button_active"), id("block/pedestal_button_e"));
    }

    private void registerColorProviders() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem) stack.getItem()).getColor(stack), PortalCubedItems.PORTAL_GUN);
    }

    private void registerEntityRenderers() {
        EntityModelLayerRegistry.registerModelLayer(ExperimentalPortalModel.MAIN_LAYER, ExperimentalPortalModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.EXPERIMENTAL_PORTAL, ExperimentalPortalRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER, StorageCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.STORAGE_CUBE, StorageCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER, CompanionCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.COMPANION_CUBE, CompanionCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RadioModel.RADIO_MAIN_LAYER, RadioModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.RADIO, RadioRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER, RedirectionCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.REDIRECTION_CUBE, RedirectionCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(OldApModel.OLD_AP_CUBE_MAIN_LAYER, OldApModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.OLDAPCUBE, OldApRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(Portal1CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER, Portal1CompanionCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, Portal1CompanionCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(Portal1StorageCubeModel.COMPANION_CUBE_MAIN_LAYER, Portal1StorageCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, Portal1StorageCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BeansModel.BEANS_LAYER, BeansModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.BEANS, BeansRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MugModel.MUG_LAYER, MugModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.MUG, MugRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(JugModel.JUG_LAYER, JugModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.JUG, JugRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ComputerModel.COMPUTER_LAYER, ComputerModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.COMPUTER, ComputerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ChairModel.CHAIR_LAYER, ChairModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.CHAIR, ChairRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(LilPineappleModel.LIL_PINEAPPLE, LilPineappleModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.LIL_PINEAPPLE, LilPineappleRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(HoopyModel.HOOPY_LAYER, HoopyModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.HOOPY, HoopyRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(AngerCoreModel.ANGER_CORE_LAYER, AngerCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.ANGER_CORE, AngerCoreRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CakeCoreModel.CAKE_CORE_LAYER, CakeCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.CAKE_CORE, CakeCoreRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CuriosityCoreModel.CURIOSITY_CORE_LAYER, CuriosityCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.CURIOSITY_CORE, CuriosityCoreRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MoralityCoreModel.MORTALITY_CORE_LAYER, MoralityCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.MORALITY_CORE, MoralityCoreRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(SpaceCoreModel.SPACE_CORE_LAYER, SpaceCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.SPACE_CORE, SpaceCoreRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(FactCoreModel.FACT_CORE_LAYER, FactCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.FACT_CORE, FactCoreRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(AdventureCoreModel.ADVENTURE_CORE_LAYER, AdventureCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.ADVENTURE_CORE, AdventureCoreRenderer::new);
    }

}
