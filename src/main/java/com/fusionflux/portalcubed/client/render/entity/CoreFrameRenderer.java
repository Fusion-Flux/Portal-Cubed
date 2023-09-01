package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.CoreFrameModel;
import com.fusionflux.portalcubed.entity.CoreFrameEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CoreFrameRenderer extends CorePhysicsRenderer<CoreFrameEntity, CoreFrameModel> {

	private static final ResourceLocation TEXTURE = id("textures/entity/core_frame.png");

	public CoreFrameRenderer(EntityRendererProvider.Context context) {
		super(context, new CoreFrameModel(context.bakeLayer(CoreFrameModel.CORE_FRAME_LAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(CoreFrameEntity entity) {
		return TEXTURE;
	}

}
