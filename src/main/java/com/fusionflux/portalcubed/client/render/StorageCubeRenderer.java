package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.StorageCubeModel;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class StorageCubeRenderer extends MobEntityRenderer<StorageCubeEntity, StorageCubeModel> {

    private final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/storage_cube.png");
    private final Identifier ACTIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/storage_cube_lit.png");

    public StorageCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new StorageCubeModel(context.getPart(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/storage_cube_e.png");
            private final Identifier EMISSIVE_ACTIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/storage_cube_lit_e.png");

            @Override
            public Identifier getEmissiveTexture(StorageCubeEntity entity) {
                if (entity.isOnButton()) {
                    return EMISSIVE_ACTIVE_TEXTURE;
                }

                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(StorageCubeEntity entity) {
        if (entity.isOnButton()) {
            return ACTIVE_TEXTURE;
        }

        return TEXTURE;
    }

}
