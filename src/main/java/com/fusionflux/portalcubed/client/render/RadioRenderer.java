package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.render.model.entity.CompanionCubeModel;
import com.fusionflux.portalcubed.client.render.model.entity.RadioModel;
import com.fusionflux.portalcubed.client.render.model.entity.RadioModel;
import com.fusionflux.portalcubed.entity.CompanionCubeEntity;
import com.fusionflux.portalcubed.entity.RadioEntity;
import com.fusionflux.portalcubed.entity.RadioEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RadioRenderer extends MobEntityRenderer<RadioEntity, RadioModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/radio.png");
    protected final CompanionCubeModel model = new CompanionCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RadioModel.RADIO_MAIN_LAYER));

    public RadioRenderer(EntityRendererFactory.Context context) {
        super(context, new RadioModel(context.getPart(RadioModel.RADIO_MAIN_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<RadioEntity, RadioModel>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/radio_e.png");
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
