package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.JugModel;
import com.fusionflux.portalcubed.entity.JugEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class JugRenderer extends CorePhysicsRenderer<JugEntity, JugModel> {
	private static final ResourceLocation BASE_TEXTURE = id("textures/entity/water_jug.png");

	public JugRenderer(EntityRendererProvider.Context context) {
		super(context, new JugModel(Minecraft.getInstance().getEntityModels().bakeLayer(JugModel.JUG_LAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(JugEntity entity) {
		return BASE_TEXTURE;
	}
}
