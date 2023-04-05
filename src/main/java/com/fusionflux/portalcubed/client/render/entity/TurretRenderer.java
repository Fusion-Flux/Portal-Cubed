package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.TurretModel;
import com.fusionflux.portalcubed.entity.TurretEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class TurretRenderer extends CorePhysicsRenderer<TurretEntity, TurretModel> {
    public static final EntityModelLayer TURRET_LAYER = new EntityModelLayer(id("turret"), "main");

    public TurretRenderer(EntityRendererFactory.Context context) {
        super(context, new TurretModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(TurretModel.TURRET_MAIN_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(TurretEntity entity) {
        return TurretModel.DEFAULT_TEXTURE;
    }
}
