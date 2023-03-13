package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.Portal1StorageCubeModel;
import com.fusionflux.portalcubed.entity.Portal1StorageCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class Portal1StorageCubeRenderer extends CorePhysicsRenderer<Portal1StorageCubeEntity, Portal1StorageCubeModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/portal_1_storage_cube.png");

    public Portal1StorageCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new Portal1StorageCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(Portal1StorageCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(Portal1StorageCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
