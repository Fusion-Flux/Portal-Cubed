package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.HoopyModel;
import com.fusionflux.portalcubed.entity.HoopyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class HoopyRenderer extends CorePhysicsRenderer<HoopyEntity, HoopyModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/hoopy.png");

    public HoopyRenderer(EntityRendererProvider.Context context) {
        super(context, new HoopyModel(Minecraft.getInstance().getEntityModels().bakeLayer(HoopyModel.HOOPY_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HoopyEntity entity) {
        return BASE_TEXTURE;
    }
}
