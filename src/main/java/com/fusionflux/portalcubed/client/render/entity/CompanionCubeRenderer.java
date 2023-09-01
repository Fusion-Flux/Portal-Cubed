package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EntityEmissiveRendering;
import com.fusionflux.portalcubed.client.render.entity.model.CompanionCubeModel;
import com.fusionflux.portalcubed.entity.CompanionCubeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CompanionCubeRenderer extends CorePhysicsRenderer<CompanionCubeEntity, CompanionCubeModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/companion_cube.png");
	private static final ResourceLocation EMISSIVE_TEXTURE = id("textures/entity/companion_cube_e.png");

	private static final ResourceLocation ACTIVE_TEXTURE = id("textures/entity/companion_cube_lit.png");
	private static final ResourceLocation EMISSIVE_ACTIVE_TEXTURE = id("textures/entity/companion_cube_lit_e.png");

	public CompanionCubeRenderer(EntityRendererProvider.Context context) {
		super(context, new CompanionCubeModel(context.bakeLayer(CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
		this.addLayer(EntityEmissiveRendering.featureRenderer(this, entity -> {
			if (entity.isOnButton()) {
				return EMISSIVE_ACTIVE_TEXTURE;
			}

			return EMISSIVE_TEXTURE;
		}));
	}

	@Override
	public ResourceLocation getTextureLocation(CompanionCubeEntity entity) {
		if (entity.isOnButton()) {
			return ACTIVE_TEXTURE;
		}

		return TEXTURE;
	}

}
