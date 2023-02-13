package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.StorageCubeModel;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class StorageCubeRenderer extends MobEntityRenderer<StorageCubeEntity, StorageCubeModel> {

    private static final Identifier TEXTURE = id("textures/entity/storage_cube.png");
    private static final Identifier ACTIVE_TEXTURE = id("textures/entity/storage_cube_lit.png");

    public StorageCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new StorageCubeModel(context.getPart(StorageCubeModel.STORAGE_CUBE_MAIN_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private static final Identifier EMISSIVE_TEXTURE = id("textures/entity/storage_cube_e.png");
            private static final Identifier EMISSIVE_ACTIVE_TEXTURE = id("textures/entity/storage_cube_lit_e.png");

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
