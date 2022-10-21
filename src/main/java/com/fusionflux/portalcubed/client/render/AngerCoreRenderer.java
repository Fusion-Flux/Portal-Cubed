package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.AngerCoreModel;
import com.fusionflux.portalcubed.client.render.model.entity.AngerCoreModel;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AngerCoreRenderer extends MobEntityRenderer<AngerCoreEntity, AngerCoreModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores.png");
    protected final AngerCoreModel model = new AngerCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(AngerCoreModel.ANGER_CORE_LAYER));

    public AngerCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new AngerCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(AngerCoreModel.ANGER_CORE_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(AngerCoreEntity entity) {
        return BASE_TEXTURE;
    }
}
