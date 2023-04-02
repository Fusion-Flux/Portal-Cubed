package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class AdvancementTitles {
    private static final AdvancementTitles NULL = new AdvancementTitles();
    private static AdvancementTitles p1, p2;

    private final Map<Identifier, AdvancementTitle> titles = new LinkedHashMap<>();
    private AdvancementTitle advancementTitleCache = null;
    private int globalAdvancementsSize = -1;

    private AdvancementTitles() {
    }

    private AdvancementTitles(JsonObject object) {
        for (final var entry : object.entrySet()) {
            titles.put(new Identifier(entry.getKey()), new AdvancementTitle(JsonHelper.asObject(entry.getValue(), entry.getKey())));
        }
    }

    public Map<Identifier, AdvancementTitle> getTitles() {
        return titles;
    }

    @Nullable
    public AdvancementTitle getTitleByGlobalAdvancements() {
        if (PortalCubedClient.globalAdvancementsSize() == globalAdvancementsSize) {
            return advancementTitleCache;
        }
        globalAdvancementsSize = PortalCubedClient.globalAdvancementsSize();
        return advancementTitleCache = titles.entrySet()
            .stream()
            .filter(entry -> PortalCubedClient.hasGlobalAdvancement(entry.getKey()))
            .reduce((a, b) -> b)
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    public static AdvancementTitles getP1() {
        return Objects.requireNonNullElse(p1, NULL);
    }

    public static AdvancementTitles getP2() {
        return Objects.requireNonNullElse(p2, NULL);
    }

    public static IdentifiableResourceReloader createResourceReloader() {
        return new IdentifiableResourceReloader() {
            @Override
            public CompletableFuture<Void> reload(
                Synchronizer synchronizer,
                ResourceManager manager,
                Profiler prepareProfiler,
                Profiler applyProfiler,
                Executor prepareExecutor,
                Executor applyExecutor
            ) {
                return synchronizer.whenPrepared(Unit.INSTANCE).thenComposeAsync(ignored -> {
                    applyProfiler.startTick();
                    applyProfiler.push("advancement_titles");
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
                    applyProfiler.pop();
                    applyProfiler.endTick();
                    return CompletableFuture.allOf(
                        Stream.of(p1, p2)
                            .map(AdvancementTitles::getTitles)
                            .map(Map::values)
                            .flatMap(Collection::stream)
                            .map(AdvancementTitle::getCubemap)
                            .map(r -> r.loadTexturesAsync(MinecraftClient.getInstance().getTextureManager(), applyExecutor))
                            .toArray(CompletableFuture[]::new)
                    );
                });
            }

            @NotNull
            @Override
            public Identifier getQuiltId() {
                return PortalCubed.id("advancement_titles");
            }
        };
    }

    public static final class AdvancementTitle {
        private final CubeMapRenderer cubemap;
        private final Vec2f angle;

        public AdvancementTitle(Identifier panorama, Vec2f angle) {
            cubemap = new CubeMapRenderer(panorama);
            this.angle = angle;
        }

        private AdvancementTitle(JsonObject object) {
            this(new Identifier(JsonHelper.getString(object, "panorama")), parseAngle(JsonHelper.getArray(object, "angle")));
        }

        private static Vec2f parseAngle(JsonArray array) {
            // These are in yaw/pitch, so y/x
            return new Vec2f(JsonHelper.asFloat(array.get(1), "1"), JsonHelper.asFloat(array.get(0), "0"));
        }

        public CubeMapRenderer getCubemap() {
            return cubemap;
        }

        public Vec2f getAngle() {
            return angle;
        }
    }

    public static class CustomCubeMapRenderer extends RotatingCubeMapRenderer {
        public boolean p1;

        public CustomCubeMapRenderer() {
            super(TitleScreen.PANORAMA_CUBE_MAP);
        }

        @Override
        public void render(float delta, float alpha) {
            final AdvancementTitle title = (p1 ? getP1() : getP2()).getTitleByGlobalAdvancements();
            if (title == null) {
                super.render(delta, alpha);
                return;
            }
            title.cubemap.draw(
                MinecraftClient.getInstance(),
                title.angle.x,
                title.angle.y + 90,
                alpha
            );
        }
    }
}
