package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.CakeCoreModel;
import com.fusionflux.portalcubed.entity.CakeCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CakeCoreRenderer extends MobEntityRenderer<CakeCoreEntity, CakeCoreModel> {

    private static final Identifier TEXTURE = id("textures/entity/portal_1_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

    public CakeCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new CakeCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CakeCoreModel.CAKE_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(CakeCoreEntity entity) {
        return TEXTURE;
    }

}
