// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.CuriosityCoreEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CuriosityCoreModel extends FizzleableModel<CuriosityCoreEntity> {
	public static final EntityModelLayer CURIOSITY_CORE_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "curiosity_core"), "main");
	private final ModelPart bb_main;

	public CuriosityCoreModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(7, 26).cuboid(4.0159F, -2.5F, -1.816F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
				.uv(7, 26).cuboid(-1.741F, -2.5F, 4.0909F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.9421F, 2.3687F, 0.0F, -0.7854F, 1.5708F));

		bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(24, 0).cuboid(-1.5F, -1.5579F, -0.4437F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.9421F, 2.3687F, 0.0F, 3.1416F, 0.0F));

		bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -3.5F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.182F, 3.8536F, -3.1416F, -0.7854F, -1.5708F));

		bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -3.5F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.182F, 3.8536F, -3.1416F, 0.7854F, -1.5708F));

		bb_main.addChild("cube_r5", ModelPartBuilder.create().uv(0, 12).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.2F))
				.uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void renderFizzled(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}