package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.BeansModel;
import com.fusionflux.portalcubed.entity.BeansEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BeansRenderer extends CorePhysicsRenderer<BeansEntity, BeansModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/beans.png");

    public BeansRenderer(EntityRendererProvider.Context context) {
        super(context, new BeansModel(Minecraft.getInstance().getEntityModels().bakeLayer(BeansModel.BEANS_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BeansEntity entity) {
        return BASE_TEXTURE;
    }
}
