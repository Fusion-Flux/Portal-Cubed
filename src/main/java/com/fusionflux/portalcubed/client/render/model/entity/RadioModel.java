// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.CompanionCubeEntity;
import com.fusionflux.portalcubed.entity.RadioEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RadioModel extends EntityModel<RadioEntity> {
	public static final EntityModelLayer RADIO_MAIN_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MODID,"radio"), "main");
	private final ModelPart bb_main;

	public RadioModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 4).mirrored().cuboid(-12.0F, -1.0F, 6.0F, 8.0F, 1.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 13).mirrored().cuboid(-11.5F, -5.0F, 6.5F, 7.0F, 4.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(8.0F, 24.0F, -8.0F));

		ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -2.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.5F, -7.0F, 8.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -2.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.5F, -7.0F, 8.0F, 0.0F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(RadioEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
}