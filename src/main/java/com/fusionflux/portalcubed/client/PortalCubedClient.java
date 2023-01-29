package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.key.GrabKeyBinding;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.*;
import com.fusionflux.portalcubed.client.render.model.block.EmissiveSpriteRegistry;
import com.fusionflux.portalcubed.client.render.model.entity.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.FaithPlateScreen;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import static com.fusionflux.portalcubed.PortalCubed.id;

@ClientOnly
public class PortalCubedClient implements ClientModInitializer {
    public static long shakeStart;

    @Override
    public void onInitializeClient(ModContainer mod) {

        HandledScreens.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);

        registerEntityRenderers();
        registerColorProviders();
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

        ModelPredicateProviderRegistry.register(
            PortalCubedBlocks.POWER_BLOCK.asItem(),
            new Identifier("level"),
            (UnclampedModelPredicateProvider)ModelPredicateProviderRegistry.get(
                Items.LIGHT, new Identifier("level")
            )
        );

        PortalBlocksLoader.initClient();
    }

    private void registerEmissiveModels() {
        // Misc
        EmissiveSpriteRegistry.register(id("block/excursion_funnel"), id("block/excursion_funnel_beam"));
        EmissiveSpriteRegistry.register(id("block/excursion_funnel_reversed"), id("block/reverse_excursion_funnel_beam"));

        EmissiveSpriteRegistry.register(id("block/faith_plate"), id("block/faith_plate_e"));
        EmissiveSpriteRegistry.register(id("block/faith_plate_active"), id("block/faith_plate_e"));

        EmissiveSpriteRegistry.register(id("block/laser"), id("block/laser_beam"));
        EmissiveSpriteRegistry.register(id("block/laser_ref_main"), id("block/laser_beam"));
        EmissiveSpriteRegistry.register(id("block/laser_ref_prev"), id("block/laser_beam"));

        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_top"), id("block/portal_2_laser_field_top"));
        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_bottom"), id("block/portal_2_laser_field_bottom"));

        EmissiveSpriteRegistry.register(id("block/portal_2_fizzler_top"), id("block/portal_2_fizzler_top"));
        EmissiveSpriteRegistry.register(id("block/portal_2_fizzler_bottom"), id("block/portal_2_fizzler_bottom"));

        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_top"), id("block/portal_2_death_fizzler_top"));
        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_bottom"), id("block/portal_2_death_fizzler_bottom"));


        EmissiveSpriteRegistry.register(id("block/portal_2_fizzler_emitter_top"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_fizzler_emitter_bottom"), id("block/grill_emitters_e"));

        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_emitter_top"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_emitter_bottom"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_emitter_top_disabled"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_laser_field_emitter_bottom_disabled"), id("block/grill_emitters_e"));

        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_emitter_top"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_emitter_bottom"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_emitter_top_disabled"), id("block/grill_emitters_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_death_fizzler_emitter_bottom_disabled"), id("block/grill_emitters_e"));

        EmissiveSpriteRegistry.register(id("block/light_bridge"), id("block/light_bridge"));

        EmissiveSpriteRegistry.register(id("block/auto_portal_lower"), id("block/auto_portal_e"));
        EmissiveSpriteRegistry.register(id("block/auto_portal_upper"), id("block/auto_portal_e"));


        EmissiveSpriteRegistry.register(id("block/portal_1_door_bottom_left"), id("block/portal_1_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_1_door_bottom_left_open"), id("block/portal_1_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_1_door_bottom_right"), id("block/portal_1_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_1_door_bottom_right_open"), id("block/portal_1_door_e"));

        EmissiveSpriteRegistry.register(id("block/portal_2_door_bottom_right"), id("block/portal_2_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_door_bottom_right_open"), id("block/portal_2_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_door_top_right"), id("block/portal_2_door_e"));
        EmissiveSpriteRegistry.register(id("block/portal_2_door_top_right_open"), id("block/portal_2_door_e"));

        // Emitters

        EmissiveSpriteRegistry.register(id("block/light_bridge_emitter"), id("block/light_bridge_emitter_e"));

        EmissiveSpriteRegistry.register(id("block/excursion_funnel_emitter_off"), id("block/funnel_wings_e"));
        EmissiveSpriteRegistry.register(id("block/excursion_funnel_emitter"), id("block/funnel_wings_active_e"));
        EmissiveSpriteRegistry.register(id("block/excursion_funnel_emitter_reversed"), id("block/funnel_wings_active_reverse_e"));

        EmissiveSpriteRegistry.register(id("block/laser_emitter_active"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_emitter_active_downward"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_emitter_active_upward"), id("block/laser_emitter_e"));

        EmissiveSpriteRegistry.register(id("block/laser_relay_active"), id("block/laser_emitter_e"));

        EmissiveSpriteRegistry.register(id("block/laser_catcher_active"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_catcher_active_downward"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_catcher_active_upward"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_catcher"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_catcher_downward"), id("block/laser_emitter_e"));
        EmissiveSpriteRegistry.register(id("block/laser_catcher_upward"), id("block/laser_emitter_e"));

        // Buttons

        EmissiveSpriteRegistry.register(id("block/floor_button"), id("block/floor_button_e"));
        EmissiveSpriteRegistry.register(id("block/pedestal_button"), id("block/pedestal_button_e"));
        EmissiveSpriteRegistry.register(id("block/floor_button_active"), id("block/floor_button_e"));
        EmissiveSpriteRegistry.register(id("block/pedestal_button_active"), id("block/pedestal_button_e"));
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
        EntityRendererRegistry.register(PortalCubedEntities.OLD_AP_CUBE, OldApRenderer::new);

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

        EntityRendererRegistry.register(PortalCubedEntities.PROPULSION_GEL_BLOB, GelBlobRenderer::new);
        EntityRendererRegistry.register(PortalCubedEntities.REPULSION_GEL_BLOB, GelBlobRenderer::new);
        EntityRendererRegistry.register(PortalCubedEntities.CONVERSION_GEL_BLOB, GelBlobRenderer::new);
        EntityRendererRegistry.register(PortalCubedEntities.ADHESION_GEL_BLOB, GelBlobRenderer::new);
    }

}
