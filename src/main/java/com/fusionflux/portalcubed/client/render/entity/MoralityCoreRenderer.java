package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.MoralityCoreModel;
import com.fusionflux.portalcubed.entity.MoralityCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class MoralityCoreRenderer extends MobEntityRenderer<MoralityCoreEntity, MoralityCoreModel> {

    private final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_1_cores.png");

    public MoralityCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new MoralityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(MoralityCoreModel.MORTALITY_CORE_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_1_cores_e.png");

            @Override
            public Identifier getEmissiveTexture(MoralityCoreEntity entity) {
                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(MoralityCoreEntity entity) {
        return TEXTURE;
    }

}
