package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.BeansModel;
import com.fusionflux.portalcubed.entity.BeansEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BeansRenderer extends CorePhysicsRenderer<BeansEntity, BeansModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/beans.png");

    public BeansRenderer(EntityRendererFactory.Context context) {
        super(context, new BeansModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(BeansModel.BEANS_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(BeansEntity entity) {
        return BASE_TEXTURE;
    }
}
