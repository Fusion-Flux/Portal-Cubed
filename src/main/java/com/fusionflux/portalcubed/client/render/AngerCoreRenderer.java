package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.AngerCoreModel;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AngerCoreRenderer extends MobEntityRenderer<AngerCoreEntity, AngerCoreModel> {

    private static final Identifier TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores.png");

    public AngerCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new AngerCoreModel(context.getPart(AngerCoreModel.ANGER_CORE_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<AngerCoreEntity, AngerCoreModel>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores_e.png");

            @Override
            public Identifier getEmissiveTexture(AngerCoreEntity entity) {
                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(AngerCoreEntity entity) {
        return TEXTURE;
    }

}
