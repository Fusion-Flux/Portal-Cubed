package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.EmissiveFeatureRenderer;
import com.fusionflux.portalcubed.client.render.entity.model.CompanionCubeModel;
import com.fusionflux.portalcubed.entity.CompanionCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CompanionCubeRenderer extends MobEntityRenderer<CompanionCubeEntity, CompanionCubeModel> {

    private final Identifier TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/companion_cube.png");
    private final Identifier ACTIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/companion_cube_lit.png");

    public CompanionCubeRenderer(EntityRendererFactory.Context context) {
        super(context, new CompanionCubeModel(context.getPart(CompanionCubeModel.COMPANION_CUBE_MAIN_LAYER)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<>(this) {

            private final Identifier EMISSIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/companion_cube_e.png");
            private final Identifier EMISSIVE_ACTIVE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/companion_cube_lit_e.png");

            @Override
            public Identifier getEmissiveTexture(CompanionCubeEntity entity) {
                if (entity.isOnButton()) {
                    return EMISSIVE_ACTIVE_TEXTURE;
                }

                return EMISSIVE_TEXTURE;
            }

        });
    }

    @Override
    public Identifier getTexture(CompanionCubeEntity entity) {
        if (entity.isOnButton()) {
            return ACTIVE_TEXTURE;
        }

        return TEXTURE;
    }

}
