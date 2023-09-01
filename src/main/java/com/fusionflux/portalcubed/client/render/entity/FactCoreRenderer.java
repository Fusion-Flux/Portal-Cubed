package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.FactCoreModel;
import com.fusionflux.portalcubed.entity.FactCoreEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FactCoreRenderer extends CorePhysicsRenderer<FactCoreEntity, FactCoreModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/portal_2_cores.png");
	private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/portal_2_cores_e.png");

	public FactCoreRenderer(EntityRendererProvider.Context context) {
		super(context, new FactCoreModel(Minecraft.getInstance().getEntityModels().bakeLayer(FactCoreModel.FACT_CORE_LAYER)), 0.5f);
		this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
	}

	@Override
	public ResourceLocation getTextureLocation(FactCoreEntity entity) {
		return TEXTURE;
	}

}
