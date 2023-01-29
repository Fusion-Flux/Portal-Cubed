package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.JugModel;
import com.fusionflux.portalcubed.entity.JugEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class JugRenderer extends MobEntityRenderer<JugEntity, JugModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/water_jug.png");
    protected final JugModel model = new JugModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(JugModel.JUG_LAYER));

    public JugRenderer(EntityRendererFactory.Context context) {
        super(context, new JugModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(JugModel.JUG_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(JugEntity entity) {
        return BASE_TEXTURE;
    }
}
