package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.RadioModel;
import com.fusionflux.portalcubed.entity.RadioEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RadioRenderer extends MobEntityRenderer<RadioEntity, RadioModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/radio.png");

    public RadioRenderer(EntityRendererFactory.Context context) {
        super(context, new RadioModel(context.getPart(RadioModel.RADIO_MAIN_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/radio_e.png");

            @Override
            public Identifier getEmissiveTexture(RadioEntity entity) {
                return EMISSIVE_TEXTURE;
            }

        });    }




    @Override
    public Identifier getTexture(RadioEntity entity) {
        return BASE_TEXTURE;
    }
}
