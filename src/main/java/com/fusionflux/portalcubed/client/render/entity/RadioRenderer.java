package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.RadioModel;
import com.fusionflux.portalcubed.entity.RadioEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RadioRenderer extends CorePhysicsRenderer<RadioEntity, RadioModel> {
    private static final ResourceLocation TEXTURE = id("textures/entity/radio.png");
    private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/radio_e.png");

    public RadioRenderer(EntityRendererProvider.Context context) {
        super(context, new RadioModel(context.bakeLayer(RadioModel.RADIO_MAIN_LAYER)), 0.5f);
        this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public ResourceLocation getTextureLocation(RadioEntity entity) {
        return TEXTURE;
    }
}
