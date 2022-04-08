package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.StorageCubeModel;
import com.fusionflux.portalcubed.entity.BridgeEntity;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class BridgeRenderer extends EntityRenderer<BridgeEntity> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/storage_cube.png");
    protected final StorageCubeModel model = new StorageCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER));

    public BridgeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @Override
    public Identifier getTexture(BridgeEntity entity) {
        return BASE_TEXTURE;
    }



}
