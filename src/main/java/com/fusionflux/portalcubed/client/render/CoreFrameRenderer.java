package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.AngerCoreModel;
import com.fusionflux.portalcubed.client.render.model.entity.CoreFrameModel;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import com.fusionflux.portalcubed.entity.CoreFrameEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CoreFrameRenderer extends MobEntityRenderer<CoreFrameEntity, CoreFrameModel> {

    private static final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/core_frame.png");

    public CoreFrameRenderer(EntityRendererFactory.Context context) {
        super(context, new CoreFrameModel(context.getPart(CoreFrameModel.CORE_FRAME_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(CoreFrameEntity entity) {
        return TEXTURE;
    }

}
