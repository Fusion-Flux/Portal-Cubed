package com.fusionflux.portalcubed.client;

import amymialee.visiblebarriers.VisibleBarriers;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.gui.FaithPlateScreen;
import com.fusionflux.portalcubed.client.gui.VelocityHelperScreen;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.*;
import com.fusionflux.portalcubed.client.render.model.block.EmissiveSpriteRegistry;
import com.fusionflux.portalcubed.client.render.model.entity.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
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
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.fusionflux.portalcubed.PortalCubed.id;

@ClientOnly
public class PortalCubedClient implements ClientModInitializer {
    public static long shakeStart;
    @Nullable public static BlockPos velocityHelperDragStart;
    private static boolean hiddenBlocksVisible;
    public static boolean allowCfg;

    @Override
    public void onInitializeClient(ModContainer mod) {

        HandledScreens.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);
        HandledScreens.register(PortalCubed.VELOCITY_HELPER_SCREEN_HANDLER, VelocityHelperScreen::new);

        registerEntityRenderers();
        registerColorProviders();
        registerEmissiveModels(mod);
        PortalCubedClientPackets.registerPackets();
        PortalCubedKeyBindings.register();

        BlockEntityRendererFactories.register(PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY, VelocityHelperRenderer::new);

        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);

        // TODO: Make this actually work
//        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
//            final BlockPos origin = velocityHelperDragStart;
//            if (origin != null) {
//                final VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//                final Vec3d end = context.camera().getPos();
//                final VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
//                final MatrixStack.Entry matrix = context.matrixStack().peek();
//                final Vec3f offset = new Vec3f(Vec3d.ofCenter(velocityHelperDragStart).subtract(end));
//                final Vec3f normal = offset.copy();
//                normal.modify(f -> -f);
//                normal.normalize();
//                vertexConsumer
//                    .vertex(matrix.getModel(), offset.getX(), offset.getY(), offset.getZ())
//                    .color(0.0f, 0.5f, 1.0f, 1.0f)
//                    .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
//                    .next();
//                vertexConsumer
//                    .vertex(matrix.getModel(), 0f, 0f, 0f)
//                    .color(1.0f, 0.5f, 0.0f, 1.0f)
//                    .normal(matrix.getNormal(), normal.getX(), normal.getY(), normal.getZ())
//                    .next();
//                immediate.draw();
//            }
//        });

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

    private void registerEmissiveModels(ModContainer mod) {
        try (final Reader reader = Files.newBufferedReader(mod.getPath("emissives.json"), StandardCharsets.UTF_8)) {
            for (final var entry : JsonHelper.deserialize(reader).entrySet()) {
                EmissiveSpriteRegistry.register(id(entry.getKey()), id(entry.getValue().getAsString()));
            }
        } catch (IOException e) {
            PortalCubed.LOGGER.error("Failed to load emissives.json", e);
        }
    }

    private void registerColorProviders() {
        ColorProviderRegistry.ITEM.register(
            (stack, tintIndex) -> tintIndex > 0 ? -1 : ((PortalGun) stack.getItem()).getSidedColor(stack),
            PortalCubedItems.PORTAL_GUN, PortalCubedItems.PORTAL_GUN_PRIMARY, PortalCubedItems.PORTAL_GUN_SECONDARY
        );
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

        EntityModelLayerRegistry.registerModelLayer(CoreFrameModel.CORE_FRAME_LAYER, CoreFrameModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.CORE_FRAME, CoreFrameRenderer::new);

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

    private static final class VisibleBarriersCompat {
        static boolean isVisible() {
            return VisibleBarriers.isVisible();
        }
    }

    public static boolean hiddenBlocksVisible() {
        return hiddenBlocksVisible || (QuiltLoader.isModLoaded("visiblebarriers") && VisibleBarriersCompat.isVisible());
    }

    public static void toggleHiddenBlocksVisible() {
        hiddenBlocksVisible = !hiddenBlocksVisible;
    }

}
