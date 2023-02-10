package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.FactCoreModel;
import com.fusionflux.portalcubed.entity.FactCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FactCoreRenderer extends MobEntityRenderer<FactCoreEntity, FactCoreModel> {

    private final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores.png");

    public FactCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new FactCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(FactCoreModel.FACT_CORE_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores_e.png");

            @Override
            public Identifier getEmissiveTexture(FactCoreEntity entity) {
                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(FactCoreEntity entity) {
        return BASE_TEXTURE;
    }

}
