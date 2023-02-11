package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.SpaceCoreModel;
import com.fusionflux.portalcubed.entity.SpaceCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SpaceCoreRenderer extends MobEntityRenderer<SpaceCoreEntity, SpaceCoreModel> {

    private static final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores_e.png");

    public SpaceCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new SpaceCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(SpaceCoreModel.SPACE_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(SpaceCoreEntity entity) {
        return TEXTURE;
    }

}
