// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.LilPineappleEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LilPineappleModel extends EntityModel<LilPineappleEntity> {
	public static final EntityModelLayer LIL_PINEAPPLE = new EntityModelLayer(new Identifier(PortalCubed.MODID,"lil_pineapple"), "main");
	private final ModelPart bb_main;

	public LilPineappleModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData bb_main_r1 = bb_main.addChild("bb_main_r1", ModelPartBuilder.create().uv(0, 17).cuboid(-4.0F, -8.0F, -5.0F, 8.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData bd = bb_main.addChild("bd", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -8.5F, -7.0F, 9.0F, 8.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(4.5F, 0.5F, 2.5F));

		ModelPartData bd_r1 = bd.addChild("bd_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.3F, -9.8F, -2.5F, 0.0F, 0.0F, -0.0873F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LilPineappleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
}