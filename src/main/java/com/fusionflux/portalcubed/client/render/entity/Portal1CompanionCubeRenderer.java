package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.Portal1CompanionCubeModel;
import com.fusionflux.portalcubed.entity.Portal1CompanionCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class Portal1CompanionCubeRenderer extends CorePhysicsRenderer<Portal1CompanionCubeEntity, Portal1CompanionCubeModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/portal_1_companion_cube.png");

    public Portal1CompanionCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new Portal1CompanionCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(Portal1CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(Portal1CompanionCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
