package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.ChairModel;
import com.fusionflux.portalcubed.entity.ChairEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class ChairRenderer extends CorePhysicsRenderer<ChairEntity, ChairModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/chair.png");

    public ChairRenderer(EntityRendererProvider.Context context) {
        super(context, new ChairModel(Minecraft.getInstance().getEntityModels().bakeLayer(ChairModel.CHAIR_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ChairEntity entity) {
        return BASE_TEXTURE;
    }
}
