package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

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
            .put("pillar", PillarBlock::new)
            .put("directional", DirectionalBlock::new)
            .put("old_ap", OldApBlock::new)
            .put("old_ap_directional", OldApDirectionalBlock::new)
            .put("facade", FacadeBlock::new)
            .put("multiface", SimpleMultiSidedBlock::new)
            .build();
    @ClientOnly
    private static Map<String, RenderLayer> renderLayers;
    private static final Map<String, BlockData> BLOCK_DATA = new LinkedHashMap<>();
    private static final ItemGroup ITEM_GROUP = QuiltItemGroup.createWithIcon(
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
            final Identifier id = id(key);
            Registry.register(Registry.BLOCK, id, value.block);
            Registry.register(Registry.ITEM, id, new BlockItem(value.block, new Item.Settings().group(ITEM_GROUP)));
        });
    }

    @ClientOnly
    public static void initClient() {
        BLOCK_DATA.forEach((key, value) -> {
            final Identifier id = id(key);
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
        final QuiltBlockSettings settings = json.has("inherit")
            ? Registry.BLOCK.getOrEmpty(new Identifier(JsonHelper.getString(json, "inherit")))
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
                case "hardness" -> settings.hardness(JsonHelper.asFloat(value, "hardness"));
                case "blast_resistance" -> settings.resistance(JsonHelper.asFloat(value, "blast_resistance"));
                case "opaque" -> settings.opaque(JsonHelper.asBoolean(value, "opaque"));
                case "jump_velocity_multiplier" -> settings.jumpVelocityMultiplier(JsonHelper.asFloat(value, "jump_velocity_multiplier"));
                case "slipperiness" -> settings.slipperiness(JsonHelper.asFloat(value, "slipperiness"));
                case "sounds" -> settings.sounds(parseBlockSounds(value));
                case "render_layer" -> renderLayer = JsonHelper.asString(value, "render_layer");
                default -> throw new IllegalArgumentException("Unknown Portal Block field " + entry.getKey());
            }
        }
        return new BlockData(type.apply(settings), renderLayer);
    }

    private static BlockSoundGroup parseBlockSounds(JsonElement sounds) {
        if (sounds.isJsonPrimitive()) {
            return Registry.BLOCK.getOrEmpty(new Identifier(JsonHelper.asString(sounds, "sounds")))
                .map(b -> b.getSoundGroup(b.getDefaultState()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown block " + sounds));
        }
        final JsonObject object = JsonHelper.asObject(sounds, "sounds");
        return new BlockSoundGroup(
            JsonHelper.getFloat(object, "volume", 1f),
            JsonHelper.getFloat(object, "pitch", 1f),
            new SoundEvent(new Identifier(JsonHelper.getString(object, "break"))),
            new SoundEvent(new Identifier(JsonHelper.getString(object, "step"))),
            new SoundEvent(new Identifier(JsonHelper.getString(object, "place"))),
            new SoundEvent(new Identifier(JsonHelper.getString(object, "hit"))),
            new SoundEvent(new Identifier(JsonHelper.getString(object, "fall")))
        );
    }

    private record BlockData(@Nullable Block block, @Nullable String renderLayer) {
    }
}
