package com.fusionflux.thinkingwithportatos.client.render;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.client.render.model.entity.CubeEntityModel;
import com.fusionflux.thinkingwithportatos.client.render.model.entity.PortalPlaceholderModel;
import com.fusionflux.thinkingwithportatos.entity.CubeEntity;
import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class PortalPlaceholderRenderer extends EntityRenderer<PortalPlaceholderEntity> {

    protected final PortalPlaceholderModel model = new PortalPlaceholderModel();

    private static final Identifier BASE_TEXTURE = new Identifier(ThinkingWithPortatos.MODID, "textures/entity/portal_placeholder.png");

    public PortalPlaceholderRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PortalPlaceholderEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(entity.pitch));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(entity.yaw));

        Direction direction = entity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(entity, tickDelta);

        //matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        //matrices.translate((double)direction.getOffsetX() * -0.5D, (double)direction.getOffsetY() * -0.5D, (double)direction.getOffsetZ() * -0.5D);
//matrices.translate(entity.dirOffset.x,entity.dirOffset.y,entity.dirOffset.z);

        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(PortalPlaceholderEntity entity) {
        return BASE_TEXTURE;
    }
}
