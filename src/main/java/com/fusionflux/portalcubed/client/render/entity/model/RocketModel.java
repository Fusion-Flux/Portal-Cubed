package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.RocketEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class RocketModel extends EntityModel<RocketEntity> {
    private final ModelPart bone;

    public RocketModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 23.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        bone.addChild(
            "cube_r1", ModelPartBuilder.create().uv(44, 51).cuboid(0.0F, -1.5F, -5.0F, 0.0F, 3.0F, 10.0F,
                                                                   new Dilation(0.0F)
            ), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        bone.addChild(
            "cube_r2", ModelPartBuilder.create().uv(44, 51).mirrored().cuboid(0.0F, -1.5F, -5.0F, 0.0F, 3.0F, 10.0F,
                                                                              new Dilation(0.0F)
            ).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(RocketEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
