package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.ComputerModel;
import com.fusionflux.portalcubed.entity.ComputerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class ComputerRenderer extends CorePhysicsRenderer<ComputerEntity, ComputerModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/computer.png");

    public ComputerRenderer(EntityRendererProvider.Context context) {
        super(context, new ComputerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ComputerModel.COMPUTER_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ComputerEntity entity) {
        return BASE_TEXTURE;
    }
}
