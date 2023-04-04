package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.RadioModel;
import com.fusionflux.portalcubed.entity.RadioEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RadioRenderer extends CorePhysicsRenderer<RadioEntity, RadioModel> {
    private static final Identifier TEXTURE = id("textures/entity/radio.png");
    private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/radio_e.png");

    public RadioRenderer(EntityRendererFactory.Context context) {
        super(context, new RadioModel(context.getPart(RadioModel.RADIO_MAIN_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(RadioEntity entity) {
        return TEXTURE;
    }
}
