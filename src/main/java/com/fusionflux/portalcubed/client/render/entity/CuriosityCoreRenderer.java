package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.CuriosityCoreModel;
import com.fusionflux.portalcubed.entity.CuriosityCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CuriosityCoreRenderer extends CorePhysicsRenderer<CuriosityCoreEntity, CuriosityCoreModel> {

    private static final Identifier TEXTURE = id("textures/entity/portal_1_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

    public CuriosityCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new CuriosityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CuriosityCoreModel.CURIOSITY_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(CuriosityCoreEntity entity) {
        return TEXTURE;
    }

}
