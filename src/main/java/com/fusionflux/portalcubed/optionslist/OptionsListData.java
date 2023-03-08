package com.fusionflux.portalcubed.optionslist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Based on https://github.com/TeamMidnightDust/MidnightLib/blob/architectury/common/src/main/java/eu/midnightdust/lib/config/MidnightConfig.java
public class OptionsListData {
    private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
    private static final Pattern DECIMAL_ONLY = Pattern.compile("-?(\\d+\\.?\\d*|\\d*\\.?\\d+|\\.)");
    private static final Pattern HEXADECIMAL_ONLY = Pattern.compile("(-?[#0-9a-fA-F]*)");

    private static final Map<Class<?>, List<EntryInfo>> ENTRIES = new IdentityHashMap<>();

    private static final Gson GSON = new GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT | Modifier.STATIC)
        .setExclusionStrategies(new MidnightConfig.HiddenAnnotationExclusionStrategy())
        .create();

    static List<EntryInfo> getEntries(OptionsListBlockEntity target) {
        List<EntryInfo> entries;
        synchronized (ENTRIES) {
            entries = ENTRIES.get(target.getClass());
            if (entries == null) {
                entries = new ArrayList<>();
                ENTRIES.put(target.getClass(), entries);
                for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        if (!field.isAnnotationPresent(MidnightConfig.Entry.class)) continue;
                        field.setAccessible(true);
                        EntryInfo info = new EntryInfo();
                        info.owner = target.getClass();
                        info.field = field;
                        if ((
                            field.isAnnotationPresent(MidnightConfig.Entry.class) ||
                                field.isAnnotationPresent(MidnightConfig.Comment.class)
                            ) &&
                            !field.isAnnotationPresent(MidnightConfig.Server.class) &&
                            !field.isAnnotationPresent(MidnightConfig.Hidden.class) && MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT
                        ) {
                            initClient(field, info);
                        }
                        if (field.isAnnotationPresent(MidnightConfig.Comment.class)) {
                            info.centered = field.getAnnotation(MidnightConfig.Comment.class).centered();
                        }
                        entries.add(info);
                    }
                }
            }
        }
        return entries;
    }

    public static void read(NbtCompound nbt, OptionsListBlockEntity target) {
        read(nbt.getString("OptionsListData"), target);
    }

    public static void read(String json, OptionsListBlockEntity target) {
        final List<EntryInfo> entries = getEntries(target);

        OptionsListBlockEntity newInstance;
        try {
            newInstance = GSON.fromJson(json, target.getClass());
        } catch (Exception ignored) {
            newInstance = null;
        }

        for (EntryInfo info : entries) {
            if (info.field.isAnnotationPresent(MidnightConfig.Entry.class)) {
                try {
                    info.value = info.field.get(newInstance);
                    info.field.set(target, info.value);
                    info.tempValue = info.value.toString();
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    public static void write(NbtCompound nbt, OptionsListBlockEntity target) {
        nbt.putString("OptionsListData", write(target));
    }

    public static String write(OptionsListBlockEntity target) {
        return GSON.toJson(target);
    }

    @ClientOnly
    private static void initClient(Field field, EntryInfo info) {
        Class<?> type = field.getType();
        MidnightConfig.Entry e = field.getAnnotation(MidnightConfig.Entry.class);
        info.width = e != null ? e.width() : 0;

        if (e != null) {
            if (!e.name().equals("")) info.name = Text.translatable(e.name());
            if (type == int.class) textField(info, Integer::parseInt, INTEGER_ONLY, (int) e.min(), (int) e.max(), true);
            else if (type == float.class) textField(info, Float::parseFloat, DECIMAL_ONLY, (float) e.min(), (float) e.max(), false);
            else if (type == double.class) textField(info, Double::parseDouble, DECIMAL_ONLY, e.min(), e.max(), false);
            else if (type == String.class || type == List.class) {
                info.max = e.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int) e.max();
                textField(info, String::length, null, Math.min(e.min(), 0), Math.max(e.max(), 1), true);
            } else if (type == boolean.class) {
                Function<Object, Text> func = value -> Text.translatable((Boolean) value ? "gui.yes" : "gui.no").formatted((Boolean) value ? Formatting.GREEN : Formatting.RED);
                info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    info.value = !(Boolean) info.value;
                    button.setMessage(func.apply(info.value));
                }, func);
            } else if (type.isEnum()) {
                List<?> values = Arrays.asList(field.getType().getEnumConstants());
                Function<Object, Text> func = value -> Text.translatable("optionslist.enum." + type.getSimpleName() + "." + info.value.toString());
                info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    int index = values.indexOf(info.value) + 1;
                    info.value = values.get(index >= values.size() ? 0 : index);
                    button.setMessage(func.apply(info.value));
                }, func);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void textField(EntryInfo info, Function<String, Number> f, Pattern pattern, double min, double max, boolean cast) {
        boolean isNumber = pattern != null;
        info.widget = (BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) (t, b) -> s -> {
            s = s.trim();
            if (!(s.isEmpty() || !isNumber || pattern.matcher(s).matches())) return false;

            Number value = 0;
            boolean inLimits = false;
            info.error = null;
            if (!(isNumber && s.isEmpty()) && !s.equals("-") && !s.equals(".")) {
                try {
                    value = f.apply(s);
                } catch (NumberFormatException e) {
                    return false;
                }
                inLimits = value.doubleValue() >= min && value.doubleValue() <= max;
                info.error = inLimits ? null : new AbstractMap.SimpleEntry<>(t, Text.literal(value.doubleValue() < min ?
                                                                                                "§cMinimum " + (isNumber ? "value" : "length") + (cast ? " is " + (int)min : " is " + min) :
                                                                                                "§cMaximum " + (isNumber ? "value" : "length") + (cast ? " is " + (int)max : " is " + max)));
            }

            info.tempValue = s;
            t.setEditableColor(inLimits ? 0xFFFFFFFF : 0xFFFF7777);
            info.inLimits = inLimits;
            b.active = ENTRIES.get(info.owner).stream().allMatch(e -> e.inLimits);

            if (inLimits && info.field.getType() != List.class)
                info.value = isNumber ? value : s;
            else if (inLimits) {
                if (((List<String>) info.value).size() == info.index) ((List<String>) info.value).add("");
                ((List<String>) info.value).set(info.index, Arrays.stream(info.tempValue.replace("[", "").replace("]", "").split(", ")).toList().get(0));
            }

            if (info.field.getAnnotation(MidnightConfig.Entry.class).isColor()) {
                if (!s.contains("#")) s = '#' + s;
                if (!HEXADECIMAL_ONLY.matcher(s).matches()) return false;
                try {
                    info.colorButton.setMessage(Text.literal("⬛").setStyle(Style.EMPTY.withColor(Color.decode(info.tempValue).getRGB())));
                } catch (Exception ignored) {
                }
            }
            return true;
        };
    }
}
