package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.AngerCoreModel;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class AngerCoreRenderer extends CorePhysicsRenderer<AngerCoreEntity, AngerCoreModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/portal_1_cores.png");
	private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

	public AngerCoreRenderer(EntityRendererProvider.Context context) {
		super(context, new AngerCoreModel(context.bakeLayer(AngerCoreModel.ANGER_CORE_LAYER)), 0.5f);
		this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
	}

	@Override
	public ResourceLocation getTextureLocation(AngerCoreEntity entity) {
		return TEXTURE;
	}

}
