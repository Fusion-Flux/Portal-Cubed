package com.fusionflux.portalcubed.client.render;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.fusionflux.portalcubed.PortalCubed;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;
import org.quiltmc.qsl.resource.loader.api.reloader.ResourceReloaderKeys.Client;

public class AnimatedEntityTextures {
	public static final ResourceLocation ATLAS_ID = PortalCubed.id("textures/atlas/animated_entities.png");
	public static final ResourceLocation ATLAS_INFO = PortalCubed.id("animated_entities");

	public static void init() {
		ResourceLoader loader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
		loader.registerReloader(Reloader.INSTANCE);
		loader.addReloaderOrdering(Client.TEXTURES, Reloader.ID);
	}

	private static class Reloader implements IdentifiableResourceReloader {
		public static final ResourceLocation ID = PortalCubed.id("animated_entity_textures");
		public static final Reloader INSTANCE = new Reloader();

		private AnimatedEntityTextureAtlasHolder atlasHolder;

		@Override
		@NotNull
		public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
											  ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
											  Executor backgroundExecutor, Executor gameExecutor) {
			if (atlasHolder == null) {
				TextureManager textures = Minecraft.getInstance().getTextureManager();
				atlasHolder = new AnimatedEntityTextureAtlasHolder(textures);
			}
			return atlasHolder.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
		}

		@Override
		@NotNull
		public ResourceLocation getQuiltId() {
			return ID;
		}
	}

	private static class AnimatedEntityTextureAtlasHolder extends TextureAtlasHolder {
		public AnimatedEntityTextureAtlasHolder(TextureManager textures) {
			super(textures, ATLAS_ID, ATLAS_INFO);
		}
	}
}
