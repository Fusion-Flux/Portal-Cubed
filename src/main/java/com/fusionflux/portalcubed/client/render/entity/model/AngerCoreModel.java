// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.AngerCoreEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AngerCoreModel extends FizzleableModel<AngerCoreEntity> {
	public static final EntityModelLayer ANGER_CORE_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "anger_core"), "main");
	private final ModelPart bb_main;

	public AngerCoreModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(11, 26).cuboid(-2.4F, -3.0F, 3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 5.0F, 3.5F, 0.0F, -0.7854F, 1.5708F));

		bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(11, 26).cuboid(-2.5F, -3.0F, 3.25F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -3.0F, 3.5F, 0.0F, -0.7854F, 1.5708F));

		bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(24, 3).cuboid(-1.5F, -1.5F, 0.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 3.425F, 0.0F, 3.1416F, 0.0F));

		bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -3.5F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.182F, 3.8536F, -3.1416F, -0.7854F, -1.5708F));

		bb_main.addChild("cube_r5", ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -3.5F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.182F, 3.8536F, -3.1416F, 0.7854F, -1.5708F));

		bb_main.addChild("cube_r6", ModelPartBuilder.create().uv(0, 12).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.2F))
			.uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void renderFizzled(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}