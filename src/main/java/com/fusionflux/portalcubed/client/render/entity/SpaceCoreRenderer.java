package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.SpaceCoreModel;
import com.fusionflux.portalcubed.entity.SpaceCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class SpaceCoreRenderer extends CorePhysicsRenderer<SpaceCoreEntity, SpaceCoreModel> {

    private static final Identifier TEXTURE = id("textures/entity/portal_2_cores.png");
    private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/portal_2_cores_e.png");

    public SpaceCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new SpaceCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(SpaceCoreModel.SPACE_CORE_LAYER)), 0.5f);
        this.addFeature(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
    }

    @Override
    public Identifier getTexture(SpaceCoreEntity entity) {
        return TEXTURE;
    }

}
