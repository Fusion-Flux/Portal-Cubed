package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.RedirectionCubeModel;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RedirectionCubeRenderer extends CorePhysicsRenderer<RedirectionCubeEntity, RedirectionCubeModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/redirection_cube.png");
    private static final ResourceLocation ACTIVE_TEXTURE = id("textures/entity/redirection_cube_lit.png");

    public RedirectionCubeRenderer(EntityRendererProvider.Context context) {
        super(context, new RedirectionCubeModel(Minecraft.getInstance().getEntityModels().bakeLayer(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(RedirectionCubeEntity entity) {
        if (entity.isOnButton()) {
            return ACTIVE_TEXTURE;
        }
        return BASE_TEXTURE;
    }
}
