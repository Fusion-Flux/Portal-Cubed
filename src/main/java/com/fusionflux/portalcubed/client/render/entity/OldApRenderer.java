package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.OldApModel;
import com.fusionflux.portalcubed.entity.OldApCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class OldApRenderer extends CorePhysicsRenderer<OldApCubeEntity, OldApModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/old_ap_cube.png");

    public OldApRenderer(EntityRendererProvider.Context context) {
        super(context, new OldApModel(Minecraft.getInstance().getEntityModels().bakeLayer(OldApModel.OLD_AP_CUBE_MAIN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(OldApCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
