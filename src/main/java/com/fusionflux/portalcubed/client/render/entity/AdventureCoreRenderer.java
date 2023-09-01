package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.AdventureCoreModel;
import com.fusionflux.portalcubed.entity.AdventureCoreEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class AdventureCoreRenderer extends CorePhysicsRenderer<AdventureCoreEntity, AdventureCoreModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/portal_2_cores.png");
	private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/portal_2_cores_e.png");

	public AdventureCoreRenderer(EntityRendererProvider.Context context) {
		super(context, new AdventureCoreModel(Minecraft.getInstance().getEntityModels().bakeLayer(AdventureCoreModel.ADVENTURE_CORE_LAYER)), 0.5f);
		this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
	}

	@Override
	public ResourceLocation getTextureLocation(AdventureCoreEntity entity) {
		return TEXTURE;
	}

}
