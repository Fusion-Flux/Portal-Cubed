package com.fusionflux.thinkingwithportatos.client;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.client.key.GrabKeyBinding;
import com.fusionflux.thinkingwithportatos.client.packet.ThinkingWithPortatosClientPackets;
import com.fusionflux.thinkingwithportatos.client.render.PortalHud;
import com.fusionflux.thinkingwithportatos.client.render.PortalPlaceholderRenderer;
import com.fusionflux.thinkingwithportatos.client.render.model.entity.PortalPlaceholderModel;
import com.fusionflux.thinkingwithportatos.entity.GelOrbEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeableItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ThinkingWithPortatosClient implements ClientModInitializer {

    public static void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.HLB_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.HLB_EMITTER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.EXCURSION_FUNNEL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.EXCURSION_FUNNEL_EMITTER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.NEUROTOXIN_EMITTER, RenderLayer.getTranslucent());
    }

    public static void registerItemRenderLayers() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem) stack.getItem()).getColor(stack), ThinkingWithPortatosItems.PORTAL_GUN);
    }

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerItemRenderLayers();
        registerBlockRenderLayers();
        ThinkingWithPortatosClientPackets.registerPackets();
        GrabKeyBinding.register();

   //     PortalGun.registerAlternateModels();


        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);

        //setupFluidRendering(ThinkingWithPortatosBlocks.STILL_TOXIC_GOO, ThinkingWithPortatosBlocks.FLOWING_TOXIC_GOO, new Identifier("thinkingwithportatos", "acid"), 0x2D1B00);
        //BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ThinkingWithPortatosBlocks.STILL_TOXIC_GOO, ThinkingWithPortatosBlocks.FLOWING_TOXIC_GOO);
    }

    private void registerEntityRenderers() {
        EntityModelLayerRegistry.registerModelLayer(PortalPlaceholderModel.MAIN_LAYER, PortalPlaceholderModel::getTexturedModelData);
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER, (context) -> new PortalPlaceholderRenderer(context));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.CUSTOM_PORTAL, (context) -> new PortalEntityRenderer(context));
        //EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer<GelOrbEntity>(dispatcher, context.getItemRenderer()));
       // EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.REPULSION_GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
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
