package com.fusionflux.portalcubed.client;

import amymialee.visiblebarriers.VisibleBarriers;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.FloorButtonBlock;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.gui.FaithPlateScreen;
import com.fusionflux.portalcubed.client.gui.VelocityHelperScreen;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.render.PortalHud;
import com.fusionflux.portalcubed.client.render.block.EmissiveSpriteRegistry;
import com.fusionflux.portalcubed.client.render.block.entity.RocketTurretModel;
import com.fusionflux.portalcubed.client.render.block.entity.RocketTurretRenderer;
import com.fusionflux.portalcubed.client.render.block.entity.VelocityHelperRenderer;
import com.fusionflux.portalcubed.client.render.entity.*;
import com.fusionflux.portalcubed.client.render.entity.model.*;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.mixin.client.AbstractSoundInstanceAccessor;
import com.fusionflux.portalcubed.mixin.client.MusicTrackerAccessor;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

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
    private static SoundInstance excursionFunnelMusic;

    @Override
    public void onInitializeClient(ModContainer mod) {

        HandledScreens.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);
        HandledScreens.register(PortalCubed.VELOCITY_HELPER_SCREEN_HANDLER, VelocityHelperScreen::new);

        registerEntityRenderers();
        registerColorProviders();
        registerEmissiveModels(mod);
        PortalCubedClientPackets.registerPackets();
        PortalCubedKeyBindings.register();

        HudRenderCallback.EVENT.register(PortalHud::renderPortalRight);
        HudRenderCallback.EVENT.register(PortalHud::renderPortalLeft);

        // Thanks to https://github.com/JulianWww/Amazia-fabric/blob/main/src/main/java/net/denanu/amazia/GUI/debug/VillagePathingOverlay.java for some code
//        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
//            final BlockPos origin = velocityHelperDragStart;
//            if (origin != null) {
//                RenderSystem.enableDepthTest();
//                RenderSystem.setShader(GameRenderer::getPositionColorShader);
//                final Tessellator tessellator = Tessellator.getInstance();
//                final BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
//                RenderSystem.disableTexture();
//                RenderSystem.disableBlend();
//                RenderSystem.lineWidth(5f);
//                bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
//
//                final Vec3d camPos = context.camera().getPos();
//
//                context.matrixStack().push();
//
//                final Frustum frustum = new Frustum(context.matrixStack().peek().getModel(), RenderSystem.getProjectionMatrix());
//                frustum.setPosition(camPos.x, camPos.y, camPos.z);
//
//                bufferBuilder.vertex(
//                    velocityHelperDragStart.getX() - camPos.x + 0.5,
//                    velocityHelperDragStart.getY() - camPos.y + 0.5,
//                    velocityHelperDragStart.getZ() - camPos.z + 0.5
//                ).color(0.0f, 0.5f, 1.0f, 1.0f).next();
//                assert MinecraftClient.getInstance().cameraEntity != null;
//                bufferBuilder.vertex(0, MinecraftClient.getInstance().cameraEntity.getEyeY() - camPos.y, 0).color(0.0f, 0.5f, 1.0f, 1.0f).next();
//
//                context.matrixStack().pop();
//
//                tessellator.draw();
//                RenderSystem.lineWidth(1f);
//                RenderSystem.enableBlend();
//                RenderSystem.enableTexture();
//            }
//        });

        ClientTickEvents.END.register(client -> {
            if (client.player == null) return;
            if (((EntityAttachments)client.player).isInFunnel()) {
                if (excursionFunnelMusic == null) {
                    excursionFunnelMusic = new PositionedSoundInstance(
                        PortalCubedSounds.TBEAM_TRAVEL, SoundCategory.BLOCKS,
                        0.1f, 1f, SoundInstance.m_mglvabhn(),
                        true, 0, SoundInstance.AttenuationType.NONE,
                        0.0, 0.0, 0.0, true
                    );
                    client.getSoundManager().play(excursionFunnelMusic);
                } else if (excursionFunnelMusic.getVolume() < 1f && excursionFunnelMusic instanceof AbstractSoundInstanceAccessor access) {
                    access.setVolume(excursionFunnelMusic.getVolume() + 0.05f);
                    if (((MusicTrackerAccessor)client.getMusicTracker()).getCurrent() instanceof AbstractSoundInstanceAccessor cAccess) {
                        cAccess.setVolume(1f - excursionFunnelMusic.getVolume() / 2);
                    }
                    client.getSoundManager().updateSoundVolume(null, 0); // If first argument is null, all it does is refresh SoundInstance volumes
                }
            } else if (excursionFunnelMusic != null) {
                if (excursionFunnelMusic.getVolume() <= 0f) {
                    client.getSoundManager().stop(excursionFunnelMusic);
                    excursionFunnelMusic = null;
                    if (((MusicTrackerAccessor)client.getMusicTracker()).getCurrent() instanceof AbstractSoundInstanceAccessor access) {
                        access.setVolume(1f);
                        client.getSoundManager().updateSoundVolume(null, 0); // See above
                    }
                } else if (excursionFunnelMusic instanceof AbstractSoundInstanceAccessor access) {
                    access.setVolume(excursionFunnelMusic.getVolume() - 0.05f);
                    if (((MusicTrackerAccessor)client.getMusicTracker()).getCurrent() instanceof AbstractSoundInstanceAccessor cAccess) {
                        cAccess.setVolume(1f - excursionFunnelMusic.getVolume() / 2);
                    }
                    client.getSoundManager().updateSoundVolume(null, 0); // See above
                }
            }
        });

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

        FloorButtonBlock.enableEasterEgg = true;
    }

    private void registerEmissiveModels(ModContainer mod) {
        try (Reader reader = Files.newBufferedReader(mod.getPath("emissives.json"), StandardCharsets.UTF_8)) {
            for (final var entry : JsonHelper.deserialize(reader).entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    for (final var value : entry.getValue().getAsJsonArray()) {
                        EmissiveSpriteRegistry.register(id(entry.getKey()), id(value.getAsString()));
                    }
                } else {
                    EmissiveSpriteRegistry.register(id(entry.getKey()), id(entry.getValue().getAsString()));
                }
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

        BlockEntityRendererFactories.register(PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY, VelocityHelperRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RocketTurretRenderer.ROCKET_TURRET_LAYER, RocketTurretModel::getTexturedModelData);
        BlockEntityRendererFactories.register(PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY, RocketTurretRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RocketRenderer.ROCKET_LAYER, RocketModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.ROCKET, RocketRenderer::new);

        EnergyPelletRenderer.init();
        EntityRendererRegistry.register(PortalCubedEntities.ENERGY_PELLET, EnergyPelletRenderer::new);
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
