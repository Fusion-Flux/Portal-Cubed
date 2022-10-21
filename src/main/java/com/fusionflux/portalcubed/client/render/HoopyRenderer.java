package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.HoopyModel;
import com.fusionflux.portalcubed.entity.HoopyEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class HoopyRenderer extends MobEntityRenderer<HoopyEntity, HoopyModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/hoopy.png");
    protected final HoopyModel model = new HoopyModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(HoopyModel.HOOPY_LAYER));

    public HoopyRenderer(EntityRendererFactory.Context context) {
        super(context, new HoopyModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(HoopyModel.HOOPY_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(HoopyEntity entity) {
        return BASE_TEXTURE;
    }
}
