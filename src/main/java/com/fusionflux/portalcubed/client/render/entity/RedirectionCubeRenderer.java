package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.RedirectionCubeModel;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RedirectionCubeRenderer extends MobEntityRenderer<RedirectionCubeEntity, RedirectionCubeModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/redirection_cube.png");
    private static final Identifier ACTIVE_TEXTURE = id("textures/entity/redirection_cube_lit.png");

    public RedirectionCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new RedirectionCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(RedirectionCubeEntity entity) {
        if (entity.isOnButton()) {
            return ACTIVE_TEXTURE;
        }
        return BASE_TEXTURE;
    }
}
