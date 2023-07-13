package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.blocks.FloorButtonBlock;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.gui.FaithPlateScreen;
import com.fusionflux.portalcubed.client.gui.VelocityHelperScreen;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.client.particle.PortalCubedParticleProviders;
import com.fusionflux.portalcubed.client.render.PortalHud;
import com.fusionflux.portalcubed.client.render.block.EmissiveSpriteRegistry;
import com.fusionflux.portalcubed.client.render.block.entity.*;
import com.fusionflux.portalcubed.client.render.entity.*;
import com.fusionflux.portalcubed.client.render.entity.model.*;
import com.fusionflux.portalcubed.client.render.portal.PortalRenderPhase;
import com.fusionflux.portalcubed.client.render.portal.PortalRendererImpl;
import com.fusionflux.portalcubed.client.render.portal.PortalRenderers;
import com.fusionflux.portalcubed.client.render.entity.animated_textures.AnimatedEntityTextures;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.mixin.client.AbstractSoundInstanceAccessor;
import com.fusionflux.portalcubed.mixin.client.DeathScreenAccessor;
import com.fusionflux.portalcubed.mixin.client.MusicManagerAccessor;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.CameraControl;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import xyz.amymialee.visiblebarriers.VisibleBarriers;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

import static com.fusionflux.portalcubed.PortalCubed.LOGGER;
import static com.fusionflux.portalcubed.PortalCubed.id;
import static org.quiltmc.qsl.command.api.client.ClientCommandManager.argument;
import static org.quiltmc.qsl.command.api.client.ClientCommandManager.literal;

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

    private static final DecimalFormat CL_SHOWPOS_FORMAT = new DecimalFormat("0.00");

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

    private static PortalRendererImpl renderer;
    private static PortalRenderers rendererType;

    public static IPQuaternion cameraInterpStart;
    public static long cameraInterpStartTime;

    public static Portal cameraTransformedThroughPortal;

    public static WorldRenderContext worldRenderContext; // QFAPI impl detail: this is a mutable singleton

    public static boolean showPos = false;

    @Override
    public void onInitializeClient(ModContainer mod) {

        MenuScreens.register(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, FaithPlateScreen::new);
        MenuScreens.register(PortalCubed.VELOCITY_HELPER_SCREEN_HANDLER, VelocityHelperScreen::new);

        registerEntityRenderers();
        registerColorProviders();
        registerEmissiveModels(mod);
        PortalCubedClientPackets.registerPackets();
        PortalCubedKeyBindings.register();
        PortalCubedParticleProviders.register();
        AnimatedEntityTextures.init();

        HudRenderCallback.EVENT.register(PortalHud::renderPortals);

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
            if (((EntityExt)client.player).isInFunnel()) {
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
                    if (((MusicManagerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor cAccess) {
                        cAccess.setVolume(1f - excursionFunnelMusic.getVolume() / 2);
                    }
                    client.getSoundManager().updateSourceVolume(null, 0); // If first argument is null, all it does is refresh SoundInstance volumes
                }
            } else if (excursionFunnelMusic != null) {
                if (excursionFunnelMusic.getVolume() <= 0f) {
                    client.getSoundManager().stop(excursionFunnelMusic);
                    excursionFunnelMusic = null;
                    if (((MusicManagerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor access) {
                        access.setVolume(1f);
                        client.getSoundManager().updateSourceVolume(null, 0); // See above
                    }
                } else if (excursionFunnelMusic instanceof AbstractSoundInstanceAccessor access) {
                    access.setVolume(excursionFunnelMusic.getVolume() - 0.05f);
                    if (((MusicManagerAccessor)client.getMusicManager()).getCurrentMusic() instanceof AbstractSoundInstanceAccessor cAccess) {
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

        // TODO: Remove this code if it's truly unnecessary
//        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlasTexture, registry) -> {
//            registry.register(toxicGooStillSpriteId);
//            registry.register(toxicGooFlowSpriteId);
//        });

        ItemProperties.register(
            PortalCubedBlocks.POWER_BLOCK.asItem(),
            new ResourceLocation("level"),
            (ClampedItemPropertyFunction)ItemProperties.getProperty(
                Items.LIGHT, new ResourceLocation("level")
            )
        );

        WorldRenderEvents.END.register(ctx -> {
            final var cameraEntity = ctx.camera().getEntity();
            if (!(ctx.consumers() instanceof final MultiBufferSource.BufferSource consumers)) return;
            final var cameraPos = ctx.camera().getPosition();
            ctx.matrixStack().pushPose();
            ctx.matrixStack().translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            PortalRenderer.renderPhase = PortalRenderPhase.TRACER;
            final EntityRenderDispatcher dispatcher = ctx.gameRenderer().getMinecraft().getEntityRenderDispatcher();
            final boolean renderHitboxes = dispatcher.shouldRenderHitBoxes();
            dispatcher.setRenderHitBoxes(false);
            for (UUID portalUuid : CalledValues.getPortals(cameraEntity)) {
                if (
                    !(((LevelExt) ctx.world()).getEntityByUuid(portalUuid) instanceof Portal portal) ||
                        !cameraEntity.getUUID().equals(portal.getOwnerUUID().orElse(null))
                ) continue;
                dispatcher.render(portal, portal.getX(), portal.getY(), portal.getZ(), portal.getYRot(), ctx.tickDelta(), ctx.matrixStack(), consumers, LightTexture.FULL_BRIGHT);
            }
            dispatcher.setRenderHitBoxes(renderHitboxes);
            ctx.matrixStack().popPose();

            RenderSystem.disableCull();
            // depth func change handled in RenderSystemMixin
            consumers.endLastBatch();
            PortalRenderer.renderPhase = PortalRenderPhase.ENTITY;
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableCull();
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

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
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
            final Matrix4f matrix = graphics.pose().last().pose();
            final float w = client.getWindow().getGuiScaledWidth();
            final float h = client.getWindow().getGuiScaledHeight();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(matrix, 0, h, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, h, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, 0, 0).color(red, 0f, 0f, alpha).endVertex();
            bufferBuilder.vertex(matrix, 0, 0, 0).color(red, 0f, 0f, alpha).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        });

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
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
            final Matrix4f matrix = graphics.pose().last().pose();
            final float w = client.getWindow().getGuiScaledWidth();
            final float h = client.getWindow().getGuiScaledHeight();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(matrix, 0, h, 0).uv(0f, 0f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, h, 0).uv(1f, 0f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, w, 0, 0).uv(1f, 1f).color(1f, 1f, 1f, alpha).endVertex();
            bufferBuilder.vertex(matrix, 0, 0, 0).uv(0f, 1f).color(1f, 1f, 1f, alpha).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        });

        //noinspection UnstableApiUsage
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(e -> e.addAfter(
            i -> i.getItem() instanceof RecordItem,
            List.of(
                new ItemStack(PortalCubedItems.STILL_ALIVE),
                new ItemStack(PortalCubedItems.CARA_MIA_ADDIO),
                new ItemStack(PortalCubedItems.WANT_YOU_GONE),
                new ItemStack(PortalCubedItems.RECONSTRUCTING_MORE_SCIENCE)
            ),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        ));

        WorldRenderEvents.START.register(context -> worldRenderContext = context);

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            if (!showPos) return;
            final Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.renderDebug) return;
            final LocalPlayer player = minecraft.player;
            if (player == null) return;

            graphics.drawString(minecraft.font, Component.literal("name: ").append(player.getName()), 2, 11, 0xffffffff);

            graphics.drawString(
                minecraft.font,
                "pos:  " + CL_SHOWPOS_FORMAT.format(player.getX()) +
                    ' ' + CL_SHOWPOS_FORMAT.format(player.getY()) +
                    ' ' + CL_SHOWPOS_FORMAT.format(player.getZ()),
                2, 20, 0xffffffff
            );

            IPQuaternion quat = IPQuaternion.getCameraRotation(player.getXRot(), player.getYRot());
            final Optional<IPQuaternion> rotation = interpCamera();
            if (rotation.isPresent()) {
                quat = quat.hamiltonProduct(rotation.get());
            }
            final Vector3d angle = quat.toQuaterniond().getEulerAnglesZXY(new Vector3d());
            graphics.drawString(
                minecraft.font,
                "ang:  " + CL_SHOWPOS_FORMAT.format(Math.toDegrees(angle.x)) +
                    ' ' + CL_SHOWPOS_FORMAT.format(Mth.wrapDegrees(Math.toDegrees(angle.y) + 180)) +
                    ' ' + CL_SHOWPOS_FORMAT.format(Math.toDegrees(angle.z)),
                2, 29, 0xffffffff
            );

            graphics.drawString(
                minecraft.font,
                "vel:  " + CL_SHOWPOS_FORMAT.format(player.getDeltaMovement().length()),
                2, 38, 0xffffffff
            );
        });

        try {
            final CompoundTag compound = NbtIo.readCompressed(GLOBAL_ADVANCEMENTS_FILE);
            for (final Tag element : compound.getList("Advancements", Tag.TAG_STRING)) {
                GLOBAL_ADVANCEMENTS.add(new ResourceLocation(element.getAsString()));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load global advancements", e);
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> {
            if (QuiltLoader.isDevelopmentEnvironment()) {
                dispatcher.register(literal("face")
                    .executes(ctx -> {
                        final Entity entity = ctx.getSource().getEntity();
                        final Direction direction = entity.getDirection();
                        entity.setXRot(entity.getXRot() < -45 ? -90 : entity.getXRot() > 45 ? 90 : 0);
                        entity.setYRot(direction.toYRot());
                        return 1;
                    })
                );
                dispatcher.register(literal("center")
                    .executes(ctx -> {
                        final Entity entity = ctx.getSource().getEntity();
                        entity.setPos(
                            entity.onGround()
                                ? Vec3.atBottomCenterOf(entity.blockPosition())
                                : Vec3.atCenterOf(entity.blockPosition())
                        );
                        return 1;
                    })
                );
            }
            dispatcher.register(literal("showpos")
                .then(argument("enabled", BoolArgumentType.bool())
                    .executes(ctx -> {
                        showPos = BoolArgumentType.getBool(ctx, "enabled");
                        ctx.getSource().sendFeedback(Component.literal("showpos = " + showPos));
                        return showPos ? 1 : 0;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.literal("showpos = " + showPos));
                    return showPos ? 1 : 0;
                })
            );
        });
    }

    public static void zoomGoBrrrr(MutableDouble fov) {
        var tickDelta = Minecraft.getInstance().getFrameTime();
        if (zoomDir == 0) return;
        if (zoomDir > 0) {
            if (zoomTimer < ZOOM_TIME) {
                fov.setValue(Mth.lerp((zoomTimer + tickDelta) / ZOOM_TIME, fov.getValue(), fov.getValue() / 2));
            } else {
                fov.setValue(fov.getValue() * .5);
            }
        } else {
            fov.setValue(Mth.lerp((zoomTimer + tickDelta) / ZOOM_TIME, fov.getValue() / 2, fov.getValue()));
        }
    }

    public static void moveCameraIfDead(Camera camera, Entity cameraEntity, CameraType perspective, float tickDelta, CameraControl ctrl) {
        var pos = ctrl.getPos();

        final var minecraft = Minecraft.getInstance();
        if (PortalCubedClient.isPortalHudMode() && minecraft.screen instanceof DeathScreen) {
            ctrl.setPos(pos.add(0, -1, 0));
        }
    }

    public static void transformCameraIntersectingPortal(Camera camera, Entity cameraEntity, CameraType perspective, float tickDelta, CameraControl ctrl) {
        final Vec3 entityPos = cameraEntity.getPosition(tickDelta);
        final Vec3 startPos = entityPos.add(ctrl.getPos().subtract(entityPos).normalize().scale(0.1));
        final Vec3 endPos = ctrl.getPos();
        final var transformed = PortalDirectionUtils.simpleTransformPassingVector(
            cameraEntity, startPos, endPos, p -> p.getNormal().y < 0
        );
        if (transformed != null) {
            cameraTransformedThroughPortal = transformed.second();
            ctrl.setPos(transformed.first());
            final Quaternionf cameraRot = camera.rotation().mul(
                cameraTransformedThroughPortal.getTransformQuat().toQuaternionf()
            );
            camera.getLookVector().set(0.0F, 0.0F, 1.0F).rotate(cameraRot);
            camera.getUpVector().set(0.0F, 1.0F, 0.0F).rotate(cameraRot);
            camera.getLeftVector().set(1.0F, 0.0F, 0.0F).rotate(cameraRot);
            if (camera.isDetached()) {
                ((CameraExt)camera).backCameraUp(transformed.first());
                ctrl.setPos(camera.getPosition());
            }
        } else {
            cameraTransformedThroughPortal = null;
        }
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
        EntityModelLayerRegistry.registerModelLayer(PortalModel.MAIN_LAYER, PortalModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.PORTAL, PortalRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER, StorageCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.STORAGE_CUBE, StorageCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER, CompanionCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.COMPANION_CUBE, CompanionCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RadioModel.RADIO_MAIN_LAYER, RadioModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.RADIO, RadioRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER, RedirectionCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.REDIRECTION_CUBE, RedirectionCubeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(SchrodingerCubeModel.SCHRODINGER_CUBE_MAIN_LAYER, SchrodingerCubeModel::getTexturedModelData);
        EntityRendererRegistry.register(PortalCubedEntities.SCHRODINGER_CUBE, SchrodingerCubeRenderer::new);

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

        EntityRendererRegistry.register(PortalCubedEntities.EXCURSION_FUNNEL, ExcursionFunnelRenderer::new);

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

        BlockEntityRenderers.register(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER_ENTITY, ExcursionFunnelEmitterBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ExcursionFunnelEmitterCenterModel.LAYER, ExcursionFunnelEmitterCenterModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ExcursionFunnelEmitterCenterModel.LAYER_REVERSED, ExcursionFunnelEmitterCenterModel::createBodyLayer);
    }

    private static final class VisibleBarriersCompat {
        static boolean isVisible() {
            return VisibleBarriers.isVisibilityEnabled();
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

    @NotNull
    public static PortalRendererImpl getRenderer() {
        if (rendererType != PortalCubedConfig.renderer) {
            rendererType = PortalCubedConfig.renderer;
            renderer = rendererType.creator.get();
        }
        return renderer;
    }

    public static Optional<IPQuaternion> interpCamera() {
        final long time = System.currentTimeMillis();
        final long endTime = PortalCubedClient.cameraInterpStartTime + PortalCubedConfig.portalSmoothTime;
        if (time >= endTime) {
            return Optional.empty();
        }
        return Optional.of(IPQuaternion.interpolate(
            PortalCubedClient.cameraInterpStart,
            IPQuaternion.IDENTITY,
            (time - PortalCubedClient.cameraInterpStartTime) / (double)PortalCubedConfig.portalSmoothTime
        ));
    }
}
