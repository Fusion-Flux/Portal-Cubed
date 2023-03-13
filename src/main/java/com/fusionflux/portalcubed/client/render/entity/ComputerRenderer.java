package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.ComputerModel;
import com.fusionflux.portalcubed.entity.ComputerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class ComputerRenderer extends CorePhysicsRenderer<ComputerEntity, ComputerModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/computer.png");

    public ComputerRenderer(EntityRendererFactory.Context context) {
        super(context, new ComputerModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ComputerModel.COMPUTER_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(ComputerEntity entity) {
        return BASE_TEXTURE;
    }
}
