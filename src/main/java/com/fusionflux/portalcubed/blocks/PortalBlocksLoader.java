package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
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

public final class PortalBlocksLoader {
    private static final Map<String, Function<QuiltBlockSettings, Block>> BLOCK_TYPES =
        ImmutableMap.<String, Function<QuiltBlockSettings, Block>>builder()
            .put("default", Block::new)
            .put("provided", settings -> null)
            .put("pillar", PillarBlock::new)
            .put("directional", DirectionalBlock::new)
            .put("old_ap", OldApBlock::new)
            .put("old_ap_directional", OldApDirectionalBlock::new)
            .put("facade", FacadeBlock::new)
            .build();
    @ClientOnly
    private static Map<String, RenderLayer> renderLayers;
    private static final Map<String, BlockData> BLOCK_DATA = new LinkedHashMap<>();

    static {
        if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
            clinitClient();
        }
    }

    private PortalBlocksLoader() {
    }

    @ClientOnly
    private static void clinitClient() {
        renderLayers = ImmutableMap.<String, RenderLayer>builder()
            .put("cutout", RenderLayer.getCutout())
            .put("translucent", RenderLayer.getTranslucent())
            .build();
    }

    public static void init(ModContainer mod) {
        try (Reader reader = Files.newBufferedReader(mod.getPath("portal_blocks.json"), StandardCharsets.UTF_8)) {
            load(JsonHelper.deserialize(reader));
        } catch (IOException e) {
            PortalCubed.LOGGER.error("Failed to load block data", e);
        }
    }

    public static void initCommon() {
        BLOCK_DATA.forEach((key, value) -> {
            if (value.block == null) return;
            final Identifier id = PortalCubed.id(key);
            Registry.register(Registry.BLOCK, id, value.block);
            Registry.register(Registry.ITEM, id, new BlockItem(value.block, new Item.Settings().group(PortalCubed.PORTAL_BLOCKS_GROUP)));
        });
    }

    @ClientOnly
    public static void initClient() {
        BLOCK_DATA.forEach((key, value) -> {
            final Identifier id = PortalCubed.id(key);
            if (value.renderLayer != null) {
                final RenderLayer renderLayer = renderLayers.get(value.renderLayer);
                if (renderLayer == null) {
                    throw new IllegalArgumentException("Unknown render_layer " + value.renderLayer);
                }
                BlockRenderLayerMap.put(
                    renderLayer,
                    Registry.BLOCK.getOrEmpty(id)
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
        final var type = BLOCK_TYPES.get(JsonHelper.getString(json, "type", "default"));
        if (type == null) {
            throw new IllegalArgumentException("Unknown type " + json.get("type"));
        }
        json.remove("type");
        final QuiltBlockSettings settings = QuiltBlockSettings.of(Material.STONE)
            .strength(3.5f, 3.5f)
            .requiresTool();
        String renderLayer = null;
        for (final var entry : json.entrySet()) {
            switch (entry.getKey()) {
                case "hardness" -> settings.hardness(entry.getValue().getAsFloat());
                case "blast_resistance" -> settings.resistance(entry.getValue().getAsFloat());
                case "opaque" -> settings.opaque(entry.getValue().getAsBoolean());
                case "render_layer" -> renderLayer = entry.getValue().getAsString();
                default -> throw new IllegalArgumentException("Unknown Portal Block field " + entry.getKey());
            }
        }
        return new BlockData(type.apply(settings), renderLayer);
    }

    private record BlockData(@Nullable Block block, @Nullable String renderLayer) {
    }
}
