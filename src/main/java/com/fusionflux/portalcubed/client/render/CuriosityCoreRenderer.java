package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.CuriosityCoreModel;
import com.fusionflux.portalcubed.entity.CuriosityCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CuriosityCoreRenderer extends MobEntityRenderer<CuriosityCoreEntity, CuriosityCoreModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores.png");
    protected final CuriosityCoreModel model = new CuriosityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CuriosityCoreModel.CURIOSITY_CORE_LAYER));

    public CuriosityCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new CuriosityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CuriosityCoreModel.CURIOSITY_CORE_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(CuriosityCoreEntity entity) {
        return BASE_TEXTURE;
    }
}
