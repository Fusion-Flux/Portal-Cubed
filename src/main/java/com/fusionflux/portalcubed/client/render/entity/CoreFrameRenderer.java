package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.CoreFrameModel;
import com.fusionflux.portalcubed.entity.CoreFrameEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class CoreFrameRenderer extends MobEntityRenderer<CoreFrameEntity, CoreFrameModel> {

    private static final Identifier TEXTURE = id("textures/entity/core_frame.png");

    public CoreFrameRenderer(EntityRendererFactory.Context context) {
        super(context, new CoreFrameModel(context.getPart(CoreFrameModel.CORE_FRAME_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(CoreFrameEntity entity) {
        return TEXTURE;
    }

}
