package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.AngerCoreModel;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class AngerCoreRenderer extends CorePhysicsRenderer<AngerCoreEntity, AngerCoreModel> {

    private static final Identifier TEXTURE = id("textures/entity/portal_1_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

    public AngerCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new AngerCoreModel(context.getPart(AngerCoreModel.ANGER_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(AngerCoreEntity entity) {
        return TEXTURE;
    }

}
