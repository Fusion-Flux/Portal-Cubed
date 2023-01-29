package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.OldApModel;
import com.fusionflux.portalcubed.entity.OldApCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class OldApRenderer extends MobEntityRenderer<OldApCubeEntity, OldApModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/old_ap_cube.png");
    protected final OldApModel model = new OldApModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(OldApModel.OLD_AP_CUBE_MAIN_LAYER));

    public OldApRenderer(EntityRendererFactory.Context context) {
        super(context, new OldApModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(OldApModel.OLD_AP_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(OldApCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
