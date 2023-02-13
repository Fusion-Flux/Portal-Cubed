package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.MoralityCoreModel;
import com.fusionflux.portalcubed.entity.MoralityCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class MoralityCoreRenderer extends MobEntityRenderer<MoralityCoreEntity, MoralityCoreModel> {

    private static final Identifier TEXTURE = id("textures/entity/portal_1_cores.png");

    public MoralityCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new MoralityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(MoralityCoreModel.MORTALITY_CORE_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

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
