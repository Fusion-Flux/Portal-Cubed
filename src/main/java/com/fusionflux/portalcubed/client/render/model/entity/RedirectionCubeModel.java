// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RedirectionCubeModel extends EntityModel<RedirectionCubeEntity> {
	public static final EntityModelLayer REDIRECTION_CUBE_MAIN_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MODID,"redirection_cube"), "main");
	private final ModelPart bb_main;

	public RedirectionCubeModel(ModelPart root) {
		//  TODO: add bone fields here!
		super(RenderLayer::getEntityTranslucent);
		this.bb_main = root.getChild("bb_main");
	}


	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-13.01F, -10.01F, 2.99F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
				.uv(0, 20).cuboid(-13.3F, -10.3F, 2.7F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
				.uv(0, 40).cuboid(-11.0F, -8.0F, 5.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));

		ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(47, 0).mirrored().cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, -4.5F, 8.0F, 0.7854F, 0.0F, -1.5708F));

		ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(47, 0).mirrored().cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, -4.5F, 8.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(47, 0).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -4.5F, 8.0F, 0.0F, 0.8727F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(RedirectionCubeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
}