package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.AdventureCoreModel;
import com.fusionflux.portalcubed.entity.AdventureCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AdventureCoreRenderer extends MobEntityRenderer<AdventureCoreEntity, AdventureCoreModel> {

    private static final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_2_cores_e.png");

    public AdventureCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new AdventureCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(AdventureCoreModel.ADVENTURE_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(AdventureCoreEntity entity) {
        return TEXTURE;
    }

}
