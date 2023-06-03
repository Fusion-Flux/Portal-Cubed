package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.TurretModel;
import com.fusionflux.portalcubed.entity.TurretEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class TurretRenderer extends CorePhysicsRenderer<TurretEntity, TurretModel> {
    public static final ModelLayerLocation TURRET_LAYER = new ModelLayerLocation(id("turret"), "main");

    public TurretRenderer(EntityRendererProvider.Context context) {
        super(context, new TurretModel(Minecraft.getInstance().getEntityModels().bakeLayer(TurretModel.TURRET_MAIN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TurretEntity entity) {
        return TurretModel.DEFAULT_TEXTURE;
    }
}
