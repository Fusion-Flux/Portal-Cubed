package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.render.model.entity.StorageCubeModel;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class StorageCubeRenderer extends MobEntityRenderer<StorageCubeEntity, StorageCubeModel> {
    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/storage_cube.png");
    protected final StorageCubeModel model = new StorageCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER));

    public StorageCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new StorageCubeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER)), 0.5f);
    }




    @Override
    public Identifier getTexture(StorageCubeEntity entity) {
        return BASE_TEXTURE;
    }
}
