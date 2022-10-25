package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.AdventureCoreModel;
import com.fusionflux.portalcubed.entity.AdventureCoreEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AdventureCoreRenderer extends MobEntityRenderer<AdventureCoreEntity, AdventureCoreModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_2_cores.png");
    protected final AdventureCoreModel model = new AdventureCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(AdventureCoreModel.ADVENTURE_CORE_LAYER));

    public AdventureCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new AdventureCoreModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(AdventureCoreModel.ADVENTURE_CORE_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(AdventureCoreEntity entity) {
        return BASE_TEXTURE;
    }
}
