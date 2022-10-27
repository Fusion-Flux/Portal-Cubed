package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.render.model.entity.ExperimentalPortalModel;
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

    private static final Identifier BASE_TEXTURE = new Identifier(PortalCubed.MODID, "textures/entity/portal_placeholder.png");
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
//System.out.println(color);
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity))), light, OverlayTexture.DEFAULT_UV, r, g, b, 1F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(ExperimentalPortal entity) {
        return BASE_TEXTURE;
    }
}
