package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.CakeCoreModel;
import com.fusionflux.portalcubed.entity.CakeCoreEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CakeCoreRenderer extends CorePhysicsRenderer<CakeCoreEntity, CakeCoreModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/portal_1_cores.png");
	private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/portal_1_cores_e.png");

	public CakeCoreRenderer(EntityRendererProvider.Context context) {
		super(context, new CakeCoreModel(Minecraft.getInstance().getEntityModels().bakeLayer(CakeCoreModel.CAKE_CORE_LAYER)), 0.5f);
		this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> EMISSIVE_TEXTURE));
	}

	@Override
	public ResourceLocation getTextureLocation(CakeCoreEntity entity) {
		return TEXTURE;
	}

}
