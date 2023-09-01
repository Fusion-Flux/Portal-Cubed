package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.Portal1CompanionCubeModel;
import com.fusionflux.portalcubed.entity.Portal1CompanionCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class Portal1CompanionCubeRenderer extends CorePhysicsRenderer<Portal1CompanionCubeEntity, Portal1CompanionCubeModel> {
	private static final ResourceLocation BASE_TEXTURE = id("textures/entity/portal_1_companion_cube.png");

	public Portal1CompanionCubeRenderer(EntityRendererProvider.Context context) {
		super(context, new Portal1CompanionCubeModel(Minecraft.getInstance().getEntityModels().bakeLayer(Portal1CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(Portal1CompanionCubeEntity entity) {
		return BASE_TEXTURE;
	}
}
