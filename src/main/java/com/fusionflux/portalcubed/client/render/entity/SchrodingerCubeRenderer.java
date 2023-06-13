package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.SchrodingerCubeModel;
import com.fusionflux.portalcubed.entity.SchrodingerCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class SchrodingerCubeRenderer extends CorePhysicsRenderer<SchrodingerCubeEntity, SchrodingerCubeModel> {
    private static final ResourceLocation BASE_TEXTURE = id("textures/entity/schrodinger_cube.png");
    private static final ResourceLocation ACTIVE_TEXTURE = id("textures/entity/schrodinger_cube_lit.png");

    public SchrodingerCubeRenderer(EntityRendererProvider.Context context) {
        super(context, new SchrodingerCubeModel(Minecraft.getInstance().getEntityModels().bakeLayer(SchrodingerCubeModel.SCHRODINGER_CUBE_MAIN_LAYER)), 0.5f);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(SchrodingerCubeEntity entity) {
        if (entity.isActive()) {
            return ACTIVE_TEXTURE;
        }
        return BASE_TEXTURE;
    }
}
