package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.Portal1StorageCubeModel;
import com.fusionflux.portalcubed.entity.Portal1StorageCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class Portal1StorageCubeRenderer extends CorePhysicsRenderer<Portal1StorageCubeEntity, Portal1StorageCubeModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/portal_1_storage_cube.png");

    public Portal1StorageCubeRenderer(EntityRendererProvider.Context context) {
        super(context, new Portal1StorageCubeModel(Minecraft.getInstance().getEntityModels().bakeLayer(Portal1StorageCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Portal1StorageCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
