package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.CakeCoreModel;
import com.fusionflux.portalcubed.entity.CakeCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CakeCoreRenderer extends MobEntityRenderer<CakeCoreEntity, CakeCoreModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores.png");
    protected final CakeCoreModel model = new CakeCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CakeCoreModel.CAKE_CORE_LAYER));

    public CakeCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new CakeCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(CakeCoreModel.CAKE_CORE_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(CakeCoreEntity entity) {
        return BASE_TEXTURE;
    }
}
