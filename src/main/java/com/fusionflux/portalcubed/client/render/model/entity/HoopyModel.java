// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.HoopyEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HoopyModel extends FizzleableModel<HoopyEntity> {
	public static final EntityModelLayer HOOPY_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "hoopy"), "main");
	private final ModelPart bb_main;

	public HoopyModel(ModelPart root) {
		//  TODO: add bone fields here!
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-13.0F, -1.0F, -13.0F, 26.0F, 1.0F, 26.0F, new Dilation(0.0F))
			.uv(0, 27).cuboid(-12.0F, -1.0F, -12.0F, 24.0F, 1.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void renderFizzled(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}