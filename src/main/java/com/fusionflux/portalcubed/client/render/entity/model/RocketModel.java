package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.RocketEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RocketModel extends FizzleableModel<RocketEntity> {
	private final ModelPart bone;

	public RocketModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 23.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		bone.addOrReplaceChild(
			"cube_r1", CubeListBuilder.create().texOffs(44, 51).addBox(0.0F, -1.5F, -5.0F, 0.0F, 3.0F, 10.0F,
																   new CubeDeformation(0.0F)
			), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		bone.addOrReplaceChild(
			"cube_r2", CubeListBuilder.create().texOffs(44, 51).mirror().addBox(0.0F, -1.5F, -5.0F, 0.0F, 3.0F, 10.0F,
																			  new CubeDeformation(0.0F)
			).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void renderFizzled(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
