package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.key.GrabKeyBinding;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.*;
import com.fusionflux.portalcubed.client.render.model.entity.BridgeModel;
import com.fusionflux.portalcubed.client.render.model.entity.CompanionCubeModel;
import com.fusionflux.portalcubed.client.render.model.entity.PortalPlaceholderModel;
import com.fusionflux.portalcubed.client.render.model.entity.StorageCubeModel;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.DyeableItem;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;

@Environment(EnvType.CLIENT)
public class PortalCubedClient implements ClientModInitializer {

    public static void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.HLB_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.HLB_EMITTER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.NEUROTOXIN_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.EXCURSION_FUNNEL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.DUEL_EXCURSION_FUNNEL_EMITTER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_EMITTER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.NEUROTOXIN_EMITTER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.CONVERSION_GEL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.PROPULSION_GEL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.REPULSION_GEL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PortalCubedBlocks.ADHESION_GEL, RenderLayer.getTranslucent());
    }

    public static void registerItemRenderLayers() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem) stack.getItem()).getColor(stack), PortalCubedItems.PORTAL_GUN);
    }

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerItemRenderLayers();
        registerBlockRenderLayers();
        PortalCubedClientPackets.registerPackets();
        GrabKeyBinding.register();

   //     PortalGun.registerAlternateModels();


        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);



        //setupFluidRendering(PortalCubedBlocks.STILL_TOXIC_GOO, PortalCubedBlocks.FLOWING_TOXIC_GOO, new Identifier("portalcubed", "acid"), 0x2D1B00);
        //BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), PortalCubedBlocks.STILL_TOXIC_GOO, PortalCubedBlocks.FLOWING_TOXIC_GOO);
    }

    private void registerEntityRenderers() {
        EntityModelLayerRegistry.registerModelLayer(PortalPlaceholderModel.MAIN_LAYER, PortalPlaceholderModel::getTexturedModelData);
        EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.PORTAL_PLACEHOLDER, PortalPlaceholderRenderer::new);
        EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.CUSTOM_PORTAL, PortalEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER, StorageCubeModel::getTexturedModelData);
        EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.STORAGE_CUBE, StorageCubeRenderer::new);


        EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.BRIDGE,BridgeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER, CompanionCubeModel::getTexturedModelData);
        EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.COMPANION_CUBE, CompanionCubeRenderer::new);
        //EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer<GelOrbEntity>(dispatcher, context.getItemRenderer()));
       // EntityRendererRegistry.INSTANCE.register(PortalCubedEntities.REPULSION_GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
    }

    /*public static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId, final int color) {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = { null, null };



        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler()
        {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }*/

}
