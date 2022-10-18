// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.ChairEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChairModel extends EntityModel<ChairEntity> {
	public static final EntityModelLayer CHAIR_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MODID,"chair"), "main");
	private final ModelPart bb_main;

	public ChairModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -0.5F, -3.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.068F, 17.0F, 0.0251F));

		ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(16, 8).cuboid(-1.0F, -3.5F, 0.5F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F))
				.uv(11, 17).cuboid(-3.5F, -3.5F, 4.5F, 7.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(0, 9).cuboid(-5.0F, -7.0F, 0.5F, 9.0F, 5.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(3.0F, -2.5F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.7751F, 7.5F, 0.068F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(0, 9).cuboid(-5.0F, -7.0F, -0.5F, 9.0F, 5.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(3.0F, -2.5F, -1.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.7751F, 7.5F, 0.068F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r4 = bone.addChild("cube_r4", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.7249F, 7.0F, 0.068F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r5 = bone.addChild("cube_r5", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.7249F, 7.0F, 0.068F, 0.0F, 0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(ChairEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
}