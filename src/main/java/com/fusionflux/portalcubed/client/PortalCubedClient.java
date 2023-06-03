package com.fusionflux.portalcubed.client;

import amymialee.visiblebarriers.VisibleBarriers;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.FloorButtonBlock;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.gui.FaithPlateScreen;
import com.fusionflux.portalcubed.client.gui.VelocityHelperScreen;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.particle.PortalCubedParticleFactories;
import com.fusionflux.portalcubed.client.render.PortalHud;
import com.fusionflux.portalcubed.client.render.block.EmissiveSpriteRegistry;
import com.fusionflux.portalcubed.client.render.block.entity.*;
import com.fusionflux.portalcubed.client.render.entity.*;
import com.fusionflux.portalcubed.client.render.entity.model.*;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.mixin.client.AbstractSoundInstanceAccessor;
import com.fusionflux.portalcubed.mixin.client.DeathScreenAccessor;
import com.fusionflux.portalcubed.mixin.client.MusicTrackerAccessor;
import com.fusionflux.portalcubed.optionslist.OptionsListScreen;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.unascribed.lib39.recoil.api.RecoilEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fusionflux.portalcubed.PortalCubed.LOGGER;
import static com.fusionflux.portalcubed.PortalCubed.id;

@ClientOnly
public class PortalCubedClient implements ClientModInitializer {
    private static final Item[] PORTAL_HUD_DESIRABLES = {
        PortalCubedItems.PORTAL_GUN,
        PortalCubedItems.PORTAL_GUN_PRIMARY,
        PortalCubedItems.PORTAL_GUN_SECONDARY,
        PortalCubedItems.PAINT_GUN,
        PortalCubedItems.CROWBAR,
        Items.AIR
    };
    public static final int ZOOM_TIME = 2;

    private static final File GLOBAL_ADVANCEMENTS_FILE = QuiltLoader.getGameDir().resolve("portal_cubed_global_advancements.dat").toFile();
    private static final Set<ResourceLocation> GLOBAL_ADVANCEMENTS = new HashSet<>();

    public static long shakeStart;
    @Nullable public static BlockPos velocityHelperDragStart;
    private static boolean hiddenBlocksVisible;
    public static boolean allowCfg;
    private static SoundInstance excursionFunnelMusic;
    private static boolean portalHudMode = false;
    public static FogSettings customFog = null;

    public static int zoomTimer;
    public static int zoomDir;

    public static int gelOverlayTimer = -1;
    public static ResourceLocation gelOverlayTexture = TextureManager.INTENTIONAL_MISSING_TEXTURE;

    @Override
    public void onInitializeClient(ModContainer mod) {

        MenuScreens.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);
        MenuScreens.register(PortalCubed.VELOCITY_HELPER_SCREEN_HANDLER, VelocityHelperScreen::new);
        MenuScreens.register(PortalCubed.OPTIONS_LIST_SCREEN_HANDLER, OptionsListScreen::new);

        registerEntityRenderers();
        registerColorProviders();
        registerEmissiveModels(mod);
        PortalCubedClientPackets.registerPackets();
        PortalCubedKeyBindings.register();
        PortalCubedParticleFactories.register();

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

        ClientTickEvents.START.register(client -> {
            if (zoomDir != 0) {
                zoomTimer++;
                if (zoomDir < 0 && zoomTimer >= ZOOM_TIME) {
                    zoomDir = 0;
                    zoomTimer = 0;
                }
            }
            if (isPortalHudMode()) {
                assert client.player != null;
                while (client.options.keyInventory.consumeClick()) {
                    PortalCubedComponents.HOLDER_COMPONENT.get(client.player).stopHolding();
                    ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
                }
                while (client.options.keyPickItem.consumeClick()) {
                    if (zoomDir == 0) {
                        zoomDir = 1;
                        zoomTimer = 0;
                    } else {
                        zoomDir = -zoomDir;
                        zoomTimer = Math.max(ZOOM_TIME - zoomTimer, 0);
                    }
                }
            } else if (zoomDir > 0) {
                zoomDir = -1;
                zoomTimer = Math.max(ZOOM_TIME - zoomTimer, 0);
            }
            if (zoomDir > 0 && zoomTimer > 100 && client.player.input.getMoveVector().lengthSquared() > 0.1) {
                zoomDir = -1;
                zoomTimer = 0;
            }
            if (gelOverlayTimer >= 0) {
                gelOverlayTimer++;
            }
        });

        ClientTickEvents.END.register(client -> {
            if (client.player == null) return;
            if (((EntityAttachments)client.player).isInFunnel()) {
                if (excursionFunnelMusic == null) {
                    excursionFunnelMusic = new SimpleSoundInstance(
                        PortalCubedSounds.TBEAM_TRAVEL, SoundSource.BLOCKS,
                        0.1f, 1f, SoundInstance.createUnseededRandom(),
                        true, 0, SoundInstance.Attenuation.NONE,
                        0.0, 0.0, 0.0, true
                    );
                    client.getSoundManager().play(excursionFunnelMusic);
                } else if (excursionFunnelMusic.getVolume() < 1f && excursionFunnelMusic instanceof AbstractSoundInstanceAccessor access) {
                    access.setVolume(excursionFunnelMusic.getVolume() + 0.05f);
                    if (((MusicTrackerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor cAccess) {
                        cAccess.setVolume(1f - excursionFunnelMusic.getVolume() / 2);
                    }
                    client.getSoundManager().updateSourceVolume(null, 0); // If first argument is null, all it does is refresh SoundInstance volumes
                }
            } else if (excursionFunnelMusic != null) {
                if (excursionFunnelMusic.getVolume() <= 0f) {
                    client.getSoundManager().stop(excursionFunnelMusic);
                    excursionFunnelMusic = null;
                    if (((MusicTrackerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor access) {
                        access.setVolume(1f);
                        client.getSoundManager().updateSourceVolume(null, 0); // See above
                    }
                } else if (excursionFunnelMusic instanceof AbstractSoundInstanceAccessor access) {
                    access.setVolume(excursionFunnelMusic.getVolume() - 0.05f);
                    if (((MusicTrackerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor cAccess) {
                        cAccess.setVolume(1f - excursionFunnelMusic.getVolume() / 2);
                    }
                    client.getSoundManager().updateSourceVolume(null, 0); // See above
                }
            }
            if (isPortalHudMode() && !client.player.shouldShowDeathScreen() && client.screen instanceof DeathScreenAccessor deathScreen && deathScreen.getDelayTicker() >= 50) {
                client.player.respawn();
            }
        });

        final ResourceLocation toxicGooStillSpriteId = id("block/toxic_goo_still");
        final ResourceLocation toxicGooFlowSpriteId = id("block/toxic_goo_flow");
        FluidRenderHandlerRegistry.INSTANCE.register(PortalCubedFluids.TOXIC_GOO.still, PortalCubedFluids.TOXIC_GOO.flowing, new SimpleFluidRenderHandler(toxicGooStillSpriteId, toxicGooFlowSpriteId));

        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlasTexture, registry) -> {
            registry.register(toxicGooStillSpriteId);
            registry.register(toxicGooFlowSpriteId);
        });

        ItemProperties.register(
            PortalCubedBlocks.POWER_BLOCK.asItem(),
            new ResourceLocation("level"),
            (ClampedItemPropertyFunction)ItemProperties.getProperty(
                Items.LIGHT, new ResourceLocation("level")
            )
        );

        WorldRenderEvents.END.register(ctx -> {
            final var player = Minecraft.getInstance().player;
            if (player != null) {
                if (!(ctx.consumers() instanceof final MultiBufferSource.BufferSource consumers)) return;
                final var cameraPos = ctx.camera().getPosition();
                ctx.matrixStack().pushPose();
                ctx.matrixStack().translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                ExperimentalPortalRenderer.renderingTracers = true;
                for (UUID portalUuid : CalledValues.getPortals(player)) {
                    if (
                        !(((Accessors) ctx.world()).getEntity(portalUuid) instanceof ExperimentalPortal portal) ||
                            !player.getUUID().equals(portal.getOwnerUUID().orElse(null))
                    ) continue;
                    Minecraft.getInstance().getEntityRenderDispatcher().render(portal, portal.getX(), portal.getY(), portal.getZ(), portal.getYRot(), ctx.tickDelta(), ctx.matrixStack(), consumers, LightTexture.FULL_BRIGHT);
                }
                ctx.matrixStack().popPose();

                RenderSystem.disableCull();
                // depth func change handled in RenderSystemMixin
                consumers.endLastBatch();
                ExperimentalPortalRenderer.renderingTracers = false;
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
                RenderSystem.enableCull();
            }
        });

        PortalBlocksLoader.initClient();

        FloorButtonBlock.enableEasterEgg = true;

        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            allowCfg = true;
            portalHudMode = false;
            customFog = null;
        });

        ClientTickEvents.START.register(client -> {
            if (client.player == null || !isPortalHudMode()) return;
            assert client.gameMode != null;
            final Inventory inventory = client.player.getInventory();
            inventory.selected = 0;
            outer:
            for (final Item desirable : PORTAL_HUD_DESIRABLES) {
                for (int i = 0, l = inventory.getContainerSize(); i < l; i++) {
                    if (inventory.getItem(i).is(desirable)) {
                        if (i != 0) {
                            for (final Slot slot : client.player.inventoryMenu.slots) {
                                if (slot.getContainerSlot() == i) {
                                    client.gameMode.handleInventoryMouseClick(0, slot.index, 0, ClickType.SWAP, client.player);
                                    break;
                                }
                            }
                        }
                        break outer;
                    }
                }
            }
            if (!inventory.offhand.get(0).isEmpty()) {
                boolean found = false;
                for (int i = 1; i < inventory.items.size(); ++i) {
                    if (inventory.items.get(i).isEmpty()) {
                        found = true;
                        client.gameMode.handleInventoryMouseClick(0, 45, i, ClickType.SWAP, client.player);
                        break;
                    }
                }
                if (!found) {
                    client.gameMode.handleInventoryMouseClick(0, 45, 1, ClickType.THROW, client.player);
                }
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (!isPortalHudMode()) return;
            final Minecraft client = Minecraft.getInstance();
            assert client.player != null;
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableBlend();
            final boolean fadeOut = !client.player.shouldShowDeathScreen() && client.screen instanceof DeathScreenAccessor;
            if (!fadeOut && client.player.getAbilities().invulnerable) return;
            final float red = fadeOut ? 0f : 1f;
            final float alpha = fadeOut
                ? Math.min(((DeathScreenAccessor)client.screen).getDelayTicker() / 40f, 1f)
                : client.player.isDeadOrDying()
                    ? 0.5f : 1f - Mth.clamp(Mth.lerp(
                        client.player.getHealth() / client.player.getMaxHealth(), 0.65f, 1f
                    ), 0f, 1f);
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            final Matrix4f matrix = matrixStack.last().pose();
            final float w = client.getWindow().getGuiScaledWidth();
            final float h = client.getWindow().getGuiScaledHeight();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(matrix, 0, h, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, h, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, 0, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, 0, 0, 0).color(red, 0f, 0f, alpha).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (gelOverlayTimer < 0) return;
            if (gelOverlayTimer > 100) {
                gelOverlayTimer = -1;
                return;
            }
            float alpha;
            if (gelOverlayTimer <= 40) {
                alpha = 1;
            } else {
                alpha = Math.max(0, 1 - (gelOverlayTimer + tickDelta - 40) / 60f);
            }
            alpha *= PortalCubedConfig.gelOverlayOpacity / 100f;
            if (alpha <= 0) return;

            final Minecraft client = Minecraft.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, gelOverlayTexture);
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            final Matrix4f matrix = matrixStack.last().pose();
            final float w = client.getWindow().getGuiScaledWidth();
            final float h = client.getWindow().getGuiScaledHeight();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(matrix, 0, h, 0).uv(0f, 0f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, h, 0).uv(1f, 0f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, 0, 0).uv(1f, 1f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, 0, 0, 0).uv(0f, 1f).color(1f, 1f, 1f, alpha).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        });

        RecoilEvents.UPDATE_FOV.register((value, tickDelta) -> {
            if (zoomDir == 0) return;
            if (zoomDir > 0) {
                if (zoomTimer < ZOOM_TIME) {
                    value.set(Mth.lerp((zoomTimer + tickDelta) / ZOOM_TIME, value.get(), value.get() / 2));
                } else {
                    value.scale(0.5f);
                }
            } else {
                value.set(Mth.lerp((zoomTimer + tickDelta) / ZOOM_TIME, value.get() / 2, value.get()));
            }
        });

        RecoilEvents.CAMERA_SETUP.register((camera, cameraEntity, perspective, tickDelta, ctrl) -> {
            final Minecraft client = Minecraft.getInstance();
            if (PortalCubedClient.isPortalHudMode() && client.screen instanceof DeathScreen) {
                ctrl.setPos(ctrl.getPos().add(0, -1, 0));
            }
        });

//        RecoilEvents.CAMERA_SETUP.register((camera, cameraEntity, perspective, tickDelta, ctrl) -> {
//            final Vec3d startPos = cameraEntity.getLerpedPos(tickDelta);
//            final Vec3d endPos = ctrl.getPos();
//            final Vec3d transformed = PortalDirectionUtils.simpleTransformPassingVector(cameraEntity, startPos, endPos);
//            if (transformed != null) {
//                ctrl.setPos(transformed);
//            }
//        });

        try {
            final CompoundTag compound = NbtIo.readCompressed(GLOBAL_ADVANCEMENTS_FILE);
            for (final Tag element : compound.getList("Advancements", Tag.TAG_STRING)) {
                GLOBAL_ADVANCEMENTS.add(new ResourceLocation(element.getAsString()));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load global advancements", e);
        }

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(AdvancementTitles.createResourceReloader());
    }

    private void registerEmissiveModels(ModContainer mod) {
        try (Reader reader = Files.newBufferedReader(mod.getPath("emissives.json"), StandardCharsets.UTF_8)) {
            for (final var entry : GsonHelper.parse(reader).entrySet()) {
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
        EntityRendererRegistry.register(PortalCubedEntities.ADHESION_GEL_BLOB, GelBlobRenderer::new);
        EntityRendererRegistry.register(PortalCubedEntities.CONVERSION_GEL_BLOB, GelBlobRenderer::new);
        EntityRendererRegistry.register(PortalCubedEntities.REFLECTION_GEL_BLOB, GelBlobRenderer::new);

        BlockEntityRenderers.register(PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY, VelocityHelperRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RocketTurretRenderer.ROCKET_TURRET_LAYER, RocketTurretModel::getTexturedModelData);
        BlockEntityRenderers.register(PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY, RocketTurretRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RocketRenderer.ROCKET_LAYER, RocketModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.ROCKET, RocketRenderer::new);

        EntityRendererRegistry.register(PortalCubedEntities.ENERGY_PELLET, EnergyPelletRenderer::new);

        BlockEntityRenderers.register(PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY, LaserEmitterRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(FaithPlateRenderer.FAITH_PLATE_LAYER, FaithPlateModel::getTexturedModelData);
        BlockEntityRenderers.register(PortalCubedBlocks.FAITH_PLATE_BLOCK_ENTITY, FaithPlateRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BetaFaithPlateRenderer.BETA_FAITH_PLATE_LAYER, BetaFaithPlateModel::getTexturedModelData);
        BlockEntityRenderers.register(PortalCubedBlocks.BETA_FAITH_PLATE_BLOCK_ENTITY, BetaFaithPlateRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(TurretRenderer.TURRET_LAYER, TurretModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.TURRET, TurretRenderer::new);
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

    public static boolean isPortalHudMode() {
        return portalHudMode || PortalCubedConfig.portalHudMode;
    }

    public static boolean isPortalHudModeServer() {
        return portalHudMode;
    }

    public static void setPortalHudMode(boolean portalHudMode) {
        PortalCubedClient.portalHudMode = portalHudMode;
    }

    public static boolean hasGlobalAdvancement(ResourceLocation advancement) {
        return GLOBAL_ADVANCEMENTS.contains(advancement);
    }

    public static void addGlobalAdvancement(ResourceLocation advancement) {
        if (GLOBAL_ADVANCEMENTS.add(advancement)) {
            try {
                final CompoundTag compound = new CompoundTag();
                final ListTag list = new ListTag();
                for (final ResourceLocation id : GLOBAL_ADVANCEMENTS) {
                    list.add(StringTag.valueOf(id.toString()));
                }
                compound.put("Advancements", list);
                NbtIo.writeCompressed(compound, GLOBAL_ADVANCEMENTS_FILE);
            } catch (Exception e) {
                LOGGER.error("Failed to save global advancements", e);
            }
        }
    }

    public static int globalAdvancementsSize() {
        return GLOBAL_ADVANCEMENTS.size();
    }
}
