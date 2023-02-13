package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.JugModel;
import com.fusionflux.portalcubed.entity.JugEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class JugRenderer extends MobEntityRenderer<JugEntity, JugModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/water_jug.png");

    public JugRenderer(EntityRendererFactory.Context context) {
        super(context, new JugModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(JugModel.JUG_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(JugEntity entity) {
        return BASE_TEXTURE;
    }
}
