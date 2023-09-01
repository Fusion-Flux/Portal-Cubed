package com.fusionflux.portalcubed;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.quiltmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalTabsLoader {
	private static final Map<String, Function<JsonObject, Predicate<CreativeModeTab.ItemDisplayParameters>>> CONDITION_TYPES = Map.of(
		"and", o -> GsonHelper.getAsJsonArray(o, "conditions")
			.asList()
			.stream()
			.map(PortalTabsLoader::parseCondition)
			.reduce(Predicate::and)
			.orElse(e -> true),
		"or", o -> GsonHelper.getAsJsonArray(o, "conditions")
			.asList()
			.stream()
			.map(PortalTabsLoader::parseCondition)
			.reduce(Predicate::or)
			.orElse(e -> false),
		"not", o -> parseCondition(o.get("condition")).negate(),
		"hasPermissions", o -> CreativeModeTab.ItemDisplayParameters::hasPermissions,
		"hasFeatures", o -> {
			final FeatureFlagSet flags = FeatureFlags.REGISTRY.fromNames(
				GsonHelper.getAsJsonArray(o, "flags")
					.asList()
					.stream()
					.map(e -> new ResourceLocation(GsonHelper.convertToString(e, "flag")))
					::iterator
			);
			return p -> flags.isSubsetOf(p.enabledFeatures());
		}
	);

	public static void load(ModContainer mod) {
		final JsonObject jsonObject;
		try (Reader reader = Files.newBufferedReader(mod.getPath("portal_tabs.json"))) {
			jsonObject = GsonHelper.parse(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		load(jsonObject);
	}

	private static void load(JsonObject jsonObject) {
		for (final var entry : jsonObject.entrySet()) {
			final CreativeModeTab.Builder builder = FabricItemGroup.builder();
			final JsonObject entryData = GsonHelper.convertToJsonObject(entry.getValue(), "tab");
			if (entryData.has("title")) {
				builder.title(Component.Serializer.fromJson(entryData.get("title")));
			}
			if (entryData.has("icon")) {
				builder.icon(parseItemStack(entryData.get("icon"), "icon"));
			}
			if (entryData.has("rightAlign") && GsonHelper.getAsBoolean(entryData, "rightAlign")) {
				builder.alignedRight();
			}
			if (entryData.has("showTitle") && !GsonHelper.getAsBoolean(entryData, "showTitle")) {
				builder.hideTitle();
			}
			if (entryData.has("scrollBar") && !GsonHelper.getAsBoolean(entryData, "scrollBar")) {
				builder.noScrollBar();
			}
			if (entryData.has("backgroundImage")) {
				builder.backgroundSuffix(GsonHelper.getAsString(entryData, "backgroundImage"));
			}
			if (entryData.has("items")) {
				final var items = GsonHelper.getAsJsonArray(entryData, "items")
					.asList()
					.stream()
					.map(e -> {
						if (e.isJsonPrimitive() || (e.isJsonObject() && !e.getAsJsonObject().has("condition"))) {
							return Pair.of(
								(Predicate<CreativeModeTab.ItemDisplayParameters>)p -> true,
								List.of(parseItemStack(e, "item").get())
							);
						}
						return Pair.of(parseCondition(e), parseItemArray((JsonObject)e));
					})
					.toList();
				builder.displayItems((itemDisplayParameters, output) -> {
					for (final var condition : items) {
						if (condition.key().test(itemDisplayParameters)) {
							output.acceptAll(condition.value());
						}
					}
				});
			}
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(entry.getKey()), builder.build());
		}
	}

	private static List<ItemStack> parseItemArray(JsonObject owner) {
		return GsonHelper.getAsJsonArray(owner, "items", new JsonArray())
			.asList()
			.stream()
			.map(e -> parseItemStack(e, "item").get())
			.toList();
	}

	private static Supplier<ItemStack> parseItemStack(JsonElement element, String memberName) {
		if (element.isJsonPrimitive()) {
			final Item item = getItem(GsonHelper.convertToString(element, memberName));
			return () -> new ItemStack(item);
		}
		final JsonObject object = GsonHelper.convertToJsonObject(element, memberName);
		final Item item = getItem(GsonHelper.getAsString(object, "id"));
		final int count = object.has("count") ? GsonHelper.getAsInt(object, "count") : 1;
		final CompoundTag tag;
		try {
			tag = object.has("nbt")
				? new TagParser(new StringReader(GsonHelper.getAsString(object, "nbt"))).readStruct()
				: null;
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}
		return () -> {
			final ItemStack stack = new ItemStack(item, count);
			if (tag != null) {
				stack.setTag(tag);
			}
			return stack;
		};
	}

	private static Predicate<CreativeModeTab.ItemDisplayParameters> parseCondition(JsonElement element) {
		final JsonObject object = GsonHelper.convertToJsonObject(element, "condition");
		return CONDITION_TYPES.get(GsonHelper.getAsString(object, "condition")).apply(object);
	}

	private static Item getItem(String id) {
		final ResourceLocation path = new ResourceLocation(id);
		final Item item = BuiltInRegistries.ITEM.get(path);
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Unknown item " + id);
		}
		return item;
	}
}
