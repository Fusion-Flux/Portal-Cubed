package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.ChairModel;
import com.fusionflux.portalcubed.entity.ChairEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class ChairRenderer extends MobEntityRenderer<ChairEntity, ChairModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/chair.png");

    public ChairRenderer(EntityRendererFactory.Context context) {
        super(context, new ChairModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ChairModel.CHAIR_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(ChairEntity entity) {
        return BASE_TEXTURE;
    }
}
