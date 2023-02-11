package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.entity.model.ExperimentalPortalModel;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class ExperimentalPortalRenderer extends EntityRenderer<ExperimentalPortal> {

    private static final Identifier SQUARE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_square_outline_closed.png");
    private static final Identifier ROUND_TEXTURE  = new Identifier(PortalCubed.MOD_ID, "textures/entity/portal_oval_outline_closed.png");
    protected final ExperimentalPortalModel model = new ExperimentalPortalModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ExperimentalPortalModel.MAIN_LAYER));

    public ExperimentalPortalRenderer(EntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ExperimentalPortal entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(entity.getYaw()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getPitch()));
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(entity.getRoll()));


        int color = entity.getColor() * -1;
        if (color == -16383998) {
            color = 1908001;
        }
        if (color == 16383998) {
            color = -1908001;
        }
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.getTexture(entity))), light, OverlayTexture.DEFAULT_UV, r, g, b, 1F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(ExperimentalPortal entity) {
        if (PortalCubedConfig.enableRoundPortals) {
            return ROUND_TEXTURE;
        }else{
            return SQUARE_TEXTURE;
        }
    }
}
