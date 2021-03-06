package com.fusionflux.fluxtech.client.render.model.entity;

import com.fusionflux.fluxtech.entity.CubeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class CubeEntityModel extends EntityModel<CubeEntity> {

    private final ModelPart base;

    public CubeEntityModel() {
        base = new ModelPart(this, 0, 0);
        base.addCuboid(-8, -8, -8, 16, 16, 16);
    }

    @Override
    public void setAngles(CubeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // translate model down
        // matrices.translate(0, 1.125, 0);

        // render cube
        base.render(matrices, vertices, light, overlay);
    }
}
