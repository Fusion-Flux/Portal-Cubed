package com.fusionflux.portalcubed.client.render.entity.animated_textures;

import java.util.Set;

import com.fusionflux.portalcubed.PortalCubed;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;
import org.quiltmc.qsl.resource.loader.api.reloader.ResourceReloaderKeys.Client;

public class AnimatedEntityTextures {

	public static void init() {
		ResourceLoader loader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
		loader.registerReloader(Reloader.INSTANCE);
		loader.addReloaderOrdering(Client.TEXTURES, Reloader.ID);
	}

	private static class Reloader extends SimplePreparableReloadListener<Set<ResourceLocation>> implements IdentifiableResourceReloader {
		public static final ResourceLocation ID = PortalCubed.id("animated_entity_textures");
		public static final FileToIdConverter LISTER = new FileToIdConverter("textures/animated_entity", ".png");
		public static final Reloader INSTANCE = new Reloader();

		@Override
		@NotNull
		protected Set<ResourceLocation> prepare(ResourceManager manager, ProfilerFiller profiler) {
			return LISTER.listMatchingResources(manager).keySet();
		}

		@Override
		protected void apply(Set<ResourceLocation> textures, ResourceManager manager, ProfilerFiller profiler) {
			TextureManager textureManager = Minecraft.getInstance().getTextureManager();
			for (ResourceLocation texture : textures) {
				textureManager.register(texture, new AnimatedTexture(texture));
			}
		}

		@Override
		@NotNull
		public ResourceLocation getQuiltId() {
			return ID;
		}
	}
}
