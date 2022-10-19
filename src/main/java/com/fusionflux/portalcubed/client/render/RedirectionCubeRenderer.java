package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.RedirectionCubeModel;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RedirectionCubeRenderer extends MobEntityRenderer<RedirectionCubeEntity, RedirectionCubeModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/redirection_cube.png");
    protected final RedirectionCubeModel model = new RedirectionCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER));

    public RedirectionCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new RedirectionCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RedirectionCubeModel.REDIRECTION_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(RedirectionCubeEntity entity) {
        return BASE_TEXTURE;
    }
}