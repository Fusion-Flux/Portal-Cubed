package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class GelBlobRenderer extends EntityRenderer<GelBlobEntity> {
    private final ModelPart.Cuboid cube;

    public GelBlobRenderer(EntityRendererFactory.Context context) {
        super(context);
        cube = new ModelPart.Cuboid(
            0, 0, // U, V
            -8, 0, -8, // X, Y, Z
            16, 16, 16, // XS, YS, ZS
            0, 0, 0, // Extra XS, Extra YS, Extra ZS
            false, // Mirror
            1, 1 // U width, V height
        );
    }

    // Math from https://github.com/Tectato/Vectorientation/blob/2bfe2fc2d2c36f8af3550df09d1b5d7938869a70/src/main/java/vectorientation/mixin/FallingBlockRendererMixin.java
    @Override
    public void render(GelBlobEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        final VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));
        matrices.push();

        final Vec3f vel = new Vec3f(entity.getVelocity());
        final float y = (vel.getY() - 0.04f * tickDelta) * 0.98f;
        float speed = (float)Math.sqrt(vel.getX() * vel.getX() + y * y + vel.getZ() * vel.getZ());
        vel.normalize();
        final float angle = (float)Math.acos(MathHelper.clamp(y, -1, 1));
        vel.set(-1 * vel.getZ(), 0, vel.getX());
        vel.normalize();
        final Quaternion rot = new Quaternion(vel, -angle, false);
        matrices.translate(0, 0.5, 0);
        matrices.multiply(rot);
        speed += 0.75f;
        matrices.scale(1 / speed, speed, 1 / speed);
        matrices.translate(0, -0.5, 0);

        final float scale = entity.getScale();
        matrices.scale(scale, scale, scale);

        cube.renderCuboid(
            matrices.peek(), consumer,
            light, 0, 1, 1, 1, 1
        );
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(GelBlobEntity entity) {
        return entity.getTexture();
    }
}
