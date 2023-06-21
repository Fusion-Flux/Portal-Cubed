package com.fusionflux.portalcubed.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class EnergySparkParticle extends Particle {
    private double xoo, yoo, zoo;

    public EnergySparkParticle(
        ClientLevel clientLevel,
        double x, double y, double z,
        double xSpeed, double ySpeed, double zSpeed
    ) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        xoo = x;
        yoo = y;
        zoo = z;
        friction = 1f;
        gravity = 0.8f;
        lifetime *= 4;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        final PoseStack poseStack = new PoseStack();
        final Vec3 position = renderInfo.getPosition();
        poseStack.translate(-position.x, -position.y, -position.z);
        final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        final VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        render0(poseStack, vertexConsumer, partialTicks);
        bufferSource.endBatch();
    }

    private void render0(PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks) {
        final float x1 = (float)Mth.lerp(partialTicks, xoo, xo);
        final float y1 = (float)Mth.lerp(partialTicks, yoo, yo);
        final float z1 = (float)Mth.lerp(partialTicks, zoo, zo);

        final float x2 = (float)Mth.lerp(partialTicks, xo, x);
        final float y2 = (float)Mth.lerp(partialTicks, yo, y);
        final float z2 = (float)Mth.lerp(partialTicks, zo, z);

        final float nx = x2 - x1;
        final float ny = y2 - y1;
        final float nz = z2 - z1;

        final PoseStack.Pose matrix = poseStack.last();
        vertexConsumer
            .vertex(matrix.pose(), x1, y1, z1)
            .color(242 / 255f, 177 / 255f, 46 / 255f, 1f)
            .normal(matrix.normal(), nx, ny, nz)
            .endVertex();
        vertexConsumer
            .vertex(matrix.pose(), x2, y2, z2)
            .color(97 / 255f, 67 / 255f, 6 / 255f, 1f)
            .normal(matrix.normal(), nx, ny, nz)
            .endVertex();
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        xoo = xo;
        yoo = yo;
        zoo = zo;
        super.tick();
        if (onGround) {
            yd = -yd * 0.75;
        }
    }
}
