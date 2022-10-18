package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.ComputerModel;
import com.fusionflux.portalcubed.entity.ComputerEntity;
import com.fusionflux.portalcubed.entity.JugEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ComputerRenderer extends MobEntityRenderer<ComputerEntity, ComputerModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/computer.png");
    protected final ComputerModel model = new ComputerModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ComputerModel.COMPUTER_LAYER));

    public ComputerRenderer(EntityRendererFactory.Context context) {
        super(context, new ComputerModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ComputerModel.COMPUTER_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(ComputerEntity entity) {
        return BASE_TEXTURE;
    }
}
