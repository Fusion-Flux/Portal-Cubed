package com.fusionflux.portalcubed.client.render.models;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.Util;

public class RenderMaterials {
	@Nullable
	private static final MaterialFinder finder = Util.make(() -> {
		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		return renderer == null ? null : renderer.materialFinder();
	});

	public static final boolean ARE_SUPPORTED = checkSupport();

	public static final Map<String, RenderMaterial> BY_NAME = new HashMap<>();

	public static final RenderMaterial DEFAULT = makeMaterial(BlendMode.DEFAULT, false);
	public static final RenderMaterial SOLID = makeMaterial(BlendMode.SOLID, false);
	public static final RenderMaterial CUTOUT = makeMaterial(BlendMode.CUTOUT, false);
	public static final RenderMaterial CUTOUT_MIPPED = makeMaterial(BlendMode.CUTOUT_MIPPED, false);
	public static final RenderMaterial TRANSLUCENT = makeMaterial(BlendMode.TRANSLUCENT, false);

	public static final RenderMaterial DEFAULT_EMISSIVE = makeMaterial(BlendMode.DEFAULT, true);
	public static final RenderMaterial SOLID_EMISSIVE = makeMaterial(BlendMode.SOLID, true);
	public static final RenderMaterial CUTOUT_EMISSIVE = makeMaterial(BlendMode.CUTOUT, true);
	public static final RenderMaterial CUTOUT_MIPPED_EMISSIVE = makeMaterial(BlendMode.CUTOUT_MIPPED, true);
	public static final RenderMaterial TRANSLUCENT_EMISSIVE = makeMaterial(BlendMode.TRANSLUCENT, true);

	public static final String SUPPORTED_TYPES = String.join(", ", BY_NAME.keySet());

	public static RenderMaterial get(BlendMode mode, boolean emissive) {
		return switch (mode) {
			case DEFAULT -> emissive ? DEFAULT_EMISSIVE : DEFAULT;
			case SOLID -> emissive ? SOLID_EMISSIVE : SOLID;
			case CUTOUT -> emissive ? CUTOUT_EMISSIVE : CUTOUT;
			case CUTOUT_MIPPED -> emissive ? CUTOUT_MIPPED_EMISSIVE : CUTOUT_MIPPED;
			case TRANSLUCENT -> emissive ? TRANSLUCENT_EMISSIVE : TRANSLUCENT;
		};
	}

	public static RenderMaterial parse(String name) {
		RenderMaterial material = BY_NAME.get(name);
		if (material == null) {
			throw new JsonParseException("Invalid render material \"" + name + "\"; must be one of: " + SUPPORTED_TYPES);
		}
		return material;
	}

	private static RenderMaterial makeMaterial(BlendMode mode, boolean emissive) {
		if (finder == null)
			return null;

		finder.clear().blendMode(mode);
		String name = mode.name().toLowerCase(Locale.ROOT);
		if (emissive) {
			finder.emissive(true)
					.disableDiffuse(true)
					.ambientOcclusion(TriState.FALSE);
			name += "_emissive";
		}
		RenderMaterial material = finder.find();
		BY_NAME.put(name, material);
		return material;
	}

	private static boolean checkSupport() {
		if (finder == null) {
			PortalCubed.LOGGER.error("No renderer present, rendering will be wrong. If you have Sodium, install Indium!");
			return false;
		}
		return true;
	}
}
