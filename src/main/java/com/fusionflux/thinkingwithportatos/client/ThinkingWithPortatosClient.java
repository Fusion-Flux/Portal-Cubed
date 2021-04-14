package com.fusionflux.thinkingwithportatos.client;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.client.key.GrabKeyBinding;
import com.fusionflux.thinkingwithportatos.client.render.CubeEntityRenderer;
import com.fusionflux.thinkingwithportatos.client.render.PhysicsFallingBlockEntityRenderer;
import com.fusionflux.thinkingwithportatos.client.render.PortalHud;
import com.fusionflux.thinkingwithportatos.client.render.PortalPlaceholderRenderer;
import com.fusionflux.thinkingwithportatos.entity.*;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.client.packet.ThinkingWithPortatosClientPackets;
import com.fusionflux.thinkingwithportatos.physics.BodyGrabbingManager;
import com.qouteall.immersive_portals.render.PortalEntityRenderer;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.item.DyeableItem;

@Environment(EnvType.CLIENT)
public class ThinkingWithPortatosClient implements ClientModInitializer {
    public static final BodyGrabbingManager bodyGrabbingManager = new BodyGrabbingManager(false);

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerItemRenderLayers();
        registerBlockRenderLayers();
        ThinkingWithPortatosClientPackets.registerPackets();
        GrabKeyBinding.register();

        PortalGun.registerAlternateModels();

        BetterClientLifecycleEvents.DISCONNECT.register((client, world) -> bodyGrabbingManager.grabInstances.clear());
        ClientTickEvents.END_CLIENT_TICK.register(client -> bodyGrabbingManager.tick());
        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.CUBE, (dispatcher, context) -> new CubeEntityRenderer(dispatcher, false));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.COMPANION_CUBE, (dispatcher, context) -> new CubeEntityRenderer(dispatcher, true));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER, (dispatcher, context) -> new PortalPlaceholderRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.CUSTOM_PORTAL, (dispatcher, context) -> new PortalEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer<GelOrbEntity>(dispatcher, context.getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.PHYSICS_FALLING_BLOCK, (dispatcher, context) -> new PhysicsFallingBlockEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ThinkingWithPortatosEntities.REPULSION_GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
    }

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
}
