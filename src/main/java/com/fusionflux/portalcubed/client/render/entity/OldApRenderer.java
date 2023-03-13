package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.OldApModel;
import com.fusionflux.portalcubed.entity.OldApCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class OldApRenderer extends CorePhysicsRenderer<OldApCubeEntity, OldApModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/old_ap_cube.png");

    public OldApRenderer(EntityRendererFactory.Context context) {
        super(context, new OldApModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(OldApModel.OLD_AP_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(OldApCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
