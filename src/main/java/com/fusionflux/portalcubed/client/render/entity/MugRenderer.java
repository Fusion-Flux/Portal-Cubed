package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.MugModel;
import com.fusionflux.portalcubed.entity.MugEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class MugRenderer extends CorePhysicsRenderer<MugEntity, MugModel> {
	private static final ResourceLocation BASE_TEXTURE0 = id("textures/entity/mug_red.png");
	private static final ResourceLocation BASE_TEXTURE1 = id("textures/entity/mug_white.png");
	private static final ResourceLocation BASE_TEXTURE2 = id("textures/entity/mug_blue.png");
	private static final ResourceLocation BASE_TEXTURE3 = id("textures/entity/mug_yellow.png");

	public MugRenderer(EntityRendererProvider.Context context) {
		super(context, new MugModel(Minecraft.getInstance().getEntityModels().bakeLayer(MugModel.MUG_LAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(MugEntity entity) {
		if (entity.getMugType() == 20) {
			entity.genMugType();
		}
		if (entity.getMugType() == 0)
			return BASE_TEXTURE0;
		if (entity.getMugType() == 1)
			return BASE_TEXTURE1;
		if (entity.getMugType() == 2)
			return BASE_TEXTURE2;
		return BASE_TEXTURE3;
	}
}
