package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec2;
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

    private final Map<ResourceLocation, AdvancementTitle> titles = new LinkedHashMap<>();
    private AdvancementTitle advancementTitleCache = null;
    private int globalAdvancementsSize = -1;

    private AdvancementTitles() {
    }

    private AdvancementTitles(JsonObject object) {
        for (final var entry : object.entrySet()) {
            titles.put(new ResourceLocation(entry.getKey()), new AdvancementTitle(GsonHelper.convertToJsonObject(entry.getValue(), entry.getKey())));
        }
    }

    public Map<ResourceLocation, AdvancementTitle> getTitles() {
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
                PreparationBarrier synchronizer,
                ResourceManager manager,
                ProfilerFiller prepareProfiler,
                ProfilerFiller applyProfiler,
                Executor prepareExecutor,
                Executor applyExecutor
            ) {
                return synchronizer.wait(Unit.INSTANCE).thenComposeAsync(ignored -> {
                    applyProfiler.startTick();
                    applyProfiler.push("advancement_titles");
                    p1 = p2 = null;
                    try {
                        final JsonObject root = GsonHelper.parse(
                            manager.openAsReader(new ResourceLocation("portalcubed:advancement_titles.json"))
                        );
                        p1 = new AdvancementTitles(GsonHelper.getAsJsonObject(root, "p1"));
                        p2 = new AdvancementTitles(GsonHelper.getAsJsonObject(root, "p2"));
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
                            .map(r -> r.preload(Minecraft.getInstance().getTextureManager(), applyExecutor))
                            .toArray(CompletableFuture[]::new)
                    );
                });
            }

            @NotNull
            @Override
            public ResourceLocation getQuiltId() {
                return PortalCubed.id("advancement_titles");
            }
        };
    }

    public static final class AdvancementTitle {
        private final CubeMap cubemap;
        private final Vec2 angle;

        public AdvancementTitle(ResourceLocation panorama, Vec2 angle) {
            cubemap = new CubeMap(panorama);
            this.angle = angle;
        }

        private AdvancementTitle(JsonObject object) {
            this(new ResourceLocation(GsonHelper.getAsString(object, "panorama")), parseAngle(GsonHelper.getAsJsonArray(object, "angle")));
        }

        private static Vec2 parseAngle(JsonArray array) {
            // These are in yaw/pitch, so y/x
            return new Vec2(GsonHelper.convertToFloat(array.get(1), "1"), GsonHelper.convertToFloat(array.get(0), "0"));
        }

        public CubeMap getCubemap() {
            return cubemap;
        }

        public Vec2 getAngle() {
            return angle;
        }
    }

    public static class CustomCubeMapRenderer extends PanoramaRenderer {
        public boolean p1;

        public CustomCubeMapRenderer() {
            super(TitleScreen.CUBE_MAP);
        }

        @Override
        public void render(float delta, float alpha) {
            final AdvancementTitle title = (p1 ? getP1() : getP2()).getTitleByGlobalAdvancements();
            if (title == null) {
                super.render(delta, alpha);
                return;
            }
            title.cubemap.render(
                Minecraft.getInstance(),
                title.angle.x,
                title.angle.y + 90,
                alpha
            );
        }
    }
}
