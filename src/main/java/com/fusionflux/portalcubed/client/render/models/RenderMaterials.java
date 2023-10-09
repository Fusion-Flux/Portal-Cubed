package com.fusionflux.portalcubed.client.render.models;

import java.util.HashMap;
import java.util.Map;

import com.fusionflux.portalcubed.PortalCubed;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;

public class RenderMaterials {
	private static final Renderer renderer = RendererAccess.INSTANCE.getRenderer();
	private static final MaterialFinder finder = renderer == null ? null : renderer.materialFinder();

	public static final boolean ARE_SUPPORTED = checkSupport();

	public static final Map<String, RenderMaterial> BY_NAME = new HashMap<>();

	public static final RenderMaterial DEFAULT = makeMaterial("default", BlendMode.DEFAULT, false);
	public static final RenderMaterial SOLID = makeMaterial("solid", BlendMode.SOLID, false);
	public static final RenderMaterial CUTOUT = makeMaterial("cutout", BlendMode.CUTOUT, false);
	public static final RenderMaterial CUTOUT_MIPPED = makeMaterial("cutout_mipped", BlendMode.CUTOUT_MIPPED, false);
	public static final RenderMaterial TRANSLUCENT = makeMaterial("translucent", BlendMode.TRANSLUCENT, false);

	public static final RenderMaterial DEFAULT_EMISSIVE = makeMaterial("default_emissive", BlendMode.DEFAULT, true);
	public static final RenderMaterial SOLID_EMISSIVE = makeMaterial("solid_emissive", BlendMode.SOLID, true);
	public static final RenderMaterial CUTOUT_EMISSIVE = makeMaterial("cutout_emissive", BlendMode.CUTOUT, true);
	public static final RenderMaterial CUTOUT_MIPPED_EMISSIVE = makeMaterial("cutout_mipped_emissive", BlendMode.CUTOUT_MIPPED, true);
	public static final RenderMaterial TRANSLUCENT_EMISSIVE = makeMaterial("translucent_emissive", BlendMode.TRANSLUCENT, true);

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

	private static RenderMaterial makeMaterial(String name, BlendMode mode, boolean emissive) {
		if (finder == null)
			return null;

		finder.clear().blendMode(mode);
		if (emissive) {
			finder.emissive(true)
					.disableDiffuse(true)
					.ambientOcclusion(TriState.FALSE);
		}
		RenderMaterial material = finder.find();
		BY_NAME.put(name, material);
		return material;
	}

	private static boolean checkSupport() {
		if (renderer == null) {
			PortalCubed.LOGGER.error("No renderer present, rendering will be wrong. If you have Sodium, install Indium!");
			return false;
		}
		return true;
	}
}
