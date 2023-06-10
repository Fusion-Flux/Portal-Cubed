package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static com.fusionflux.portalcubed.PortalCubed.id;

public final class PortalBlocksLoader {
    private static final Map<String, Function<QuiltBlockSettings, Block>> BLOCK_TYPES =
        ImmutableMap.<String, Function<QuiltBlockSettings, Block>>builder()
            .put("default", Block::new)
            .put("provided", settings -> null)
            .put("pillar", RotatedPillarBlock::new)
            .put("directional", DirectionalBlock::new)
            .put("old_ap", OldApBlock::new)
            .put("old_ap_directional", OldApDirectionalBlock::new)
            .put("facade", FacadeBlock::new)
            .put("multiface", SimpleMultiSidedBlock::new)
            .build();
    @ClientOnly
    private static Map<String, RenderType> renderLayers;
    private static final Map<String, BlockData> BLOCK_DATA = new LinkedHashMap<>();
    ItemGroupEvents
    private static final CreativeModeTab ITEM_GROUP = QuiltItemGroup.createWithIcon(
        id("portal_blocks"),
        () -> new ItemStack(PortalCubedItems.BLOCK_ITEM_ICON)
    );

    static {
        if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
            clinitClient();
        }
    }

    private PortalBlocksLoader() {
    }

    @ClientOnly
    private static void clinitClient() {
        renderLayers = ImmutableMap.<String, RenderType>builder()
            .put("cutout", RenderType.cutout())
            .put("translucent", RenderType.translucent())
            .build();
    }

    public static void init(ModContainer mod) {
        try (Reader reader = Files.newBufferedReader(mod.getPath("portal_blocks.json"), StandardCharsets.UTF_8)) {
            load(GsonHelper.parse(reader));
        } catch (IOException e) {
            PortalCubed.LOGGER.error("Failed to load block data", e);
        }
    }

    public static void initCommon() {
        BLOCK_DATA.forEach((key, value) -> {
            if (value.block == null) return;
            final ResourceLocation id = id(key);
            Registry.register(Registry.BLOCK, id, value.block);
            Registry.register(Registry.ITEM, id, new BlockItem(value.block, new Item.Properties().tab(ITEM_GROUP)));
        });
    }

    @ClientOnly
    public static void initClient() {
        BLOCK_DATA.forEach((key, value) -> {
            final ResourceLocation id = id(key);
            if (value.renderLayer != null) {
                final RenderType renderLayer = renderLayers.get(value.renderLayer);
                if (renderLayer == null) {
                    throw new IllegalArgumentException("Unknown render_layer " + value.renderLayer);
                }
                BlockRenderLayerMap.put(
                    renderLayer,
                    Registry.BLOCK.getOptional(id)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown block in portal_blocks.json " + id))
                );
            }
        });
    }

    private static void load(JsonObject json) {
        for (final var entry : json.entrySet()) {
            BLOCK_DATA.put(entry.getKey(), parseBlock(entry.getValue().getAsJsonObject()));
        }
    }

    private static BlockData parseBlock(JsonObject json) {
        final var type = BLOCK_TYPES.get(GsonHelper.getAsString(json, "type", "default"));
        if (type == null) {
            throw new IllegalArgumentException("Unknown type " + json.get("type"));
        }
        json.remove("type");
        final QuiltBlockSettings settings = json.has("inherit")
            ? Registry.BLOCK.getOptional(new ResourceLocation(GsonHelper.getAsString(json, "inherit")))
                .map(QuiltBlockSettings::copyOf)
                .orElseThrow(() -> new IllegalArgumentException("Unknown block " + json.get("inherit")))
            : QuiltBlockSettings.of(Material.STONE)
                .strength(3.5f, 3.5f)
                .requiresTool();
        json.remove("inherit");
        String renderLayer = null;
        for (final var entry : json.entrySet()) {
            final JsonElement value = entry.getValue();
            switch (entry.getKey()) {
                case "hardness" -> settings.destroyTime(GsonHelper.convertToFloat(value, "hardness"));
                case "blast_resistance" -> settings.explosionResistance(GsonHelper.convertToFloat(value, "blast_resistance"));
                case "opaque" -> settings.opaque(GsonHelper.convertToBoolean(value, "opaque"));
                case "jump_velocity_multiplier" -> settings.jumpFactor(GsonHelper.convertToFloat(value, "jump_velocity_multiplier"));
                case "slipperiness" -> settings.friction(GsonHelper.convertToFloat(value, "slipperiness"));
                case "sounds" -> settings.sound(parseBlockSounds(value));
                case "render_layer" -> renderLayer = GsonHelper.convertToString(value, "render_layer");
                default -> throw new IllegalArgumentException("Unknown Portal Block field " + entry.getKey());
            }
        }
        return new BlockData(type.apply(settings), renderLayer);
    }

    private static SoundType parseBlockSounds(JsonElement sounds) {
        if (sounds.isJsonPrimitive()) {
            return Registry.BLOCK.getOptional(new ResourceLocation(GsonHelper.convertToString(sounds, "sounds")))
                .map(b -> b.getSoundType(b.defaultBlockState()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown block " + sounds));
        }
        final JsonObject object = GsonHelper.convertToJsonObject(sounds, "sounds");
        return new SoundType(
            GsonHelper.getAsFloat(object, "volume", 1f),
            GsonHelper.getAsFloat(object, "pitch", 1f),
            new SoundEvent(new ResourceLocation(GsonHelper.getAsString(object, "break"))),
            new SoundEvent(new ResourceLocation(GsonHelper.getAsString(object, "step"))),
            new SoundEvent(new ResourceLocation(GsonHelper.getAsString(object, "place"))),
            new SoundEvent(new ResourceLocation(GsonHelper.getAsString(object, "hit"))),
            new SoundEvent(new ResourceLocation(GsonHelper.getAsString(object, "fall")))
        );
    }

    private record BlockData(@Nullable Block block, @Nullable String renderLayer) {
    }
}
