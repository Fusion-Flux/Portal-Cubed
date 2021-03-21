// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

package com.fusionflux.thinkingwithportatos.client.render.model.entity;

import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class PortalPlaceholderModel extends EntityModel<PortalPlaceholderEntity> {
    private final ModelPart bb_main;

    public PortalPlaceholderModel() {
        textureWidth = 32;
        textureHeight = 32;
        bb_main = new ModelPart(this);
        bb_main.setPivot(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(0, 0).addCuboid(-8.0F, -32.0F, 0.0F, 16.0F, 32.0F, 0.0F, 0.0F, false);
    }

    @Override
    public void setAngles(PortalPlaceholderEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }


}