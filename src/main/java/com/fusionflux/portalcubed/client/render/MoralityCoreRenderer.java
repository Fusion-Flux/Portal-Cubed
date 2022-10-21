package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.MoralityCoreModel;
import com.fusionflux.portalcubed.entity.MoralityCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class MoralityCoreRenderer extends MobEntityRenderer<MoralityCoreEntity, MoralityCoreModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_1_cores.png");
    protected final MoralityCoreModel model = new MoralityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(MoralityCoreModel.MORTALITY_CORE_LAYER));

    public MoralityCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new MoralityCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(MoralityCoreModel.MORTALITY_CORE_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(MoralityCoreEntity entity) {
        return BASE_TEXTURE;
    }
}
