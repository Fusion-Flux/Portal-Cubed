// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.model.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RedirectionCubeModel extends FizzleableModel<RedirectionCubeEntity> {
	public static final EntityModelLayer REDIRECTION_CUBE_MAIN_LAYER = new EntityModelLayer(new Identifier(PortalCubed.MOD_ID, "redirection_cube"), "main");
	private final ModelPart bb_main;

	public RedirectionCubeModel(ModelPart root) {
		//  TODO: add bone fields here!
		super(RenderLayer::getEntityTranslucent);
		this.bb_main = root.getChild("bb_main");
	}


	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.99F, -9.99F, -5.01F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
				.uv(0, 20).cuboid(-4.95F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.4F))
				.uv(0, 40).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(47, 0).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.7854F, 0.0F, -1.5708F));

		bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(47, 0).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

		bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(47, 0).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, 0.8727F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void renderFizzled(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}