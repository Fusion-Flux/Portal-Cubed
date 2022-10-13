// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StorageCubeModel extends EntityModel<StorageCubeEntity> {
	public static final EntityModelLayer STORAGE_CUBE_MAIN_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MODID,"storage_cube"), "main");
	private final ModelPart bb_main;

	public StorageCubeModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static ModelData getModelData(){
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		return modelData;
	}


	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
				.uv(0, 20).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(StorageCubeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}
	
}