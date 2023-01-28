package com.fusionflux.portalcubed.client.render.model.block;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.ResourceReloaderKeys;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import com.fusionflux.portalcubed.PortalCubed;

import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public final class SpriteFinderCache implements SimpleSynchronousResourceReloader {

	public static final SpriteFinderCache INSTANCE = new SpriteFinderCache();

	private static final ThreadLocal<SpriteFinder> BLOCK_ATLAS = ThreadLocal.withInitial(() -> {
		return SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
	});

	public static final Identifier RELOADER_ID = PortalCubed.id("sprite_finder_cache");
	public static final Identifier RELOADER_ORDERING = ResourceReloaderKeys.Client.MODELS;

	public static SpriteFinder forBlockAtlas() {
		return BLOCK_ATLAS.get();
	}

	@Override
	public void reload(ResourceManager manager) {
		BLOCK_ATLAS.set(SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)));
	}

	@Override
	public @NotNull Identifier getQuiltId() {
		return RELOADER_ID;
	}

}
