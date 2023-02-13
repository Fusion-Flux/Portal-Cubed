package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.SpaceCoreModel;
import com.fusionflux.portalcubed.entity.SpaceCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SpaceCoreRenderer extends MobEntityRenderer<SpaceCoreEntity, SpaceCoreModel> {

    private static final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores.png");

    public SpaceCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new SpaceCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(SpaceCoreModel.SPACE_CORE_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private static final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores_e.png");

            @Override
            public Identifier getEmissiveTexture(SpaceCoreEntity entity) {
                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(SpaceCoreEntity entity) {
        return TEXTURE;
    }

}
