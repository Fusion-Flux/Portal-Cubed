package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class AdvancementTitles {
    private static final AdvancementTitles NULL = new AdvancementTitles();
    private static AdvancementTitles p1, p2;

    private final Map<Identifier, AdvancementTitle> titles = new LinkedHashMap<>();

    private AdvancementTitles() {
    }

    private AdvancementTitles(JsonObject object) {
        for (final var entry : object.entrySet()) {
            titles.put(new Identifier(entry.getKey()), new AdvancementTitle(JsonHelper.asObject(entry.getValue(), entry.getKey())));
        }
    }

    @Nullable
    public AdvancementTitle getTitleByGlobalAdvancements() {
        return titles.entrySet()
            .stream()
            .filter(entry -> PortalCubedClient.hasGlobalAdvancement(entry.getKey()))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    public static AdvancementTitles getP1() {
        return Objects.requireNonNullElse(p1, NULL);
    }

    public static AdvancementTitles getP2() {
        return Objects.requireNonNullElse(p2, NULL);
    }

    public static SimpleSynchronousResourceReloader createResourceReloader() {
        return new SimpleSynchronousResourceReloader() {
            @Override
            public void reload(ResourceManager manager) {
                p1 = p2 = null;
                try {
                    final JsonObject root = JsonHelper.deserialize(
                        manager.openAsReader(new Identifier("portalcubed:advancement_titles.json"))
                    );
                    p1 = new AdvancementTitles(JsonHelper.getObject(root, "p1"));
                    p2 = new AdvancementTitles(JsonHelper.getObject(root, "p2"));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @NotNull
            @Override
            public Identifier getQuiltId() {
                return PortalCubed.id("advancement_titles");
            }
        };
    }

    public record AdvancementTitle(Identifier panorama, Vec2f angle) {
        private AdvancementTitle(JsonObject object) {
            this(new Identifier(JsonHelper.getString(object, "panorama")), parseAngle(JsonHelper.getArray(object, "angle")));
        }

        private static Vec2f parseAngle(JsonArray array) {
            // These are in yaw/pitch, so y/x
            return new Vec2f(JsonHelper.asFloat(array.get(1), "1"), JsonHelper.asFloat(array.get(0), "0"));
        }
    }
}
