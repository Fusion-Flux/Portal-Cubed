package com.fusionflux.portalcubed.client.render.block;

import com.fusionflux.portalcubed.PortalCubed;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

public class RenderMaterials {
    public static final boolean ARE_SUPPORTED;
    public static final MaterialFinder FINDER;
    public static final RenderMaterial DEFAULT_MATERIAL;
    public static final RenderMaterial SOLID_MATERIAL;
    public static final RenderMaterial CUTOUT_MATERIAL;
    public static final RenderMaterial CUTOUT_MIPPED_MATERIAL;
    public static final RenderMaterial TRANSLUCENT_MATERIAL;

    static {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        ARE_SUPPORTED = renderer != null;
        if (ARE_SUPPORTED) {
            FINDER = renderer.materialFinder();
            DEFAULT_MATERIAL = FINDER.find();
            SOLID_MATERIAL = FINDER.blendMode(BlendMode.SOLID).find();
            CUTOUT_MATERIAL = FINDER.blendMode(BlendMode.CUTOUT).find();
            CUTOUT_MIPPED_MATERIAL = FINDER.blendMode(BlendMode.CUTOUT_MIPPED).find();
            TRANSLUCENT_MATERIAL = FINDER.blendMode(BlendMode.TRANSLUCENT).find();
        } else {
            PortalCubed.LOGGER.error("No renderer present, rendering will be wrong. If you have Sodium, install Indium!");
            FINDER = null;
            DEFAULT_MATERIAL = SOLID_MATERIAL = CUTOUT_MATERIAL = CUTOUT_MIPPED_MATERIAL = TRANSLUCENT_MATERIAL = null;
        }
    }

}
