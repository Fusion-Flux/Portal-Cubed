package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.key.GrabKeyBinding;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.*;
import com.fusionflux.portalcubed.client.render.model.entity.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.DyeableItem;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;


@Environment(EnvType.CLIENT)
public class PortalCubedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        registerEntityRenderers();
        registerItemRenderLayers();
        registerBlockRenderLayers();
        PortalCubedClientPackets.registerPackets();
        GrabKeyBinding.register();
        
        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);
    }
    
    public static void registerBlockRenderLayers() {
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
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.PORTAL1DOOR);
        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), PortalCubedBlocks.PORTAL2DOOR);
    }

    public static void registerItemRenderLayers() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem) stack.getItem()).getColor(stack), PortalCubedItems.PORTAL_GUN);
    }

    private void registerEntityRenderers() {
        EntityModelLayerRegistry.registerModelLayer(PortalPlaceholderModel.MAIN_LAYER, PortalPlaceholderModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.PORTAL_PLACEHOLDER, PortalPlaceholderRenderer::new);

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
    }
}
