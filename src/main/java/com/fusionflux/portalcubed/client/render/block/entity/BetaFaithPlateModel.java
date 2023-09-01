package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BetaFaithPlateModel extends FaithPlateModel {
	public static final ResourceLocation TEXTURE = id("textures/block/faith_plate.png");
	public static final ResourceLocation TEXTURE_E = id("textures/block/faith_plate_e.png");

	public static final ResourceLocation ANIMATION_FORWARD = id("beta_faith_plate/forward");
	public static final ResourceLocation ANIMATION_UPWARD = id("beta_faith_plate/upward");

	public BetaFaithPlateModel(ModelPart root) {
		super(root);
	}

	@SuppressWarnings("checkstyle:LocalVariableName")
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 15.3696F, -2.787F));

		bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 5).addBox(-0.5F, -2.5F, -3.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.4946F, 5.037F, 0.3927F, 0.0F, 0.0F));

		bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 15).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6196F, 0.037F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, -4.6712F, 7.4931F));

		bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(44, 7).addBox(-0.5F, -2.5F, -1.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5484F, -4.4561F, -0.3927F, 0.0F, 0.0F));

		bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0516F, -1.4561F, 0.7854F, 0.0F, 0.0F));

		bone2.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0167F, -2.3333F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
			.texOffs(30, 18).addBox(-1.1167F, -1.3333F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(30, 18).addBox(1.1333F, -1.3333F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0167F, -2.3651F, -4.7061F));

		PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(22, 15).addBox(1.0F, 11.5F, -15.0F, 14.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
			.texOffs(36, 4).addBox(15.0F, 0.5F, -15.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
			.texOffs(30, 32).addBox(14.99F, -0.51F, -16.01F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
			.texOffs(0, 26).addBox(0.99F, -0.51F, -1.01F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(12, 31).addBox(-0.01F, -0.51F, -16.01F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
			.texOffs(0, 28).addBox(0.99F, -0.51F, -16.01F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 7.5F, 8.0F));

		base.addOrReplaceChild("cubeinverted_r1", CubeListBuilder.create().texOffs(36, 4).addBox(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 16.5F, -8.0F, 0.0F, -1.5708F, 0.0F));

		base.addOrReplaceChild("cubeinverted_r2", CubeListBuilder.create().texOffs(36, 4).addBox(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 16.5F, -8.0F, 0.0F, 3.1416F, 0.0F));

		base.addOrReplaceChild("cubeinverted_r3", CubeListBuilder.create().texOffs(36, 4).addBox(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 16.5F, -8.0F, 0.0F, 1.5708F, 0.0F));

		modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(21, 16).addBox(-2.65F, -10.75F, -4.75F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(21, 16).addBox(2.65F, -10.75F, -4.75F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	protected ResourceLocation getForwardAnimation() {
		return ANIMATION_FORWARD;
	}

	@Override
	protected ResourceLocation getUpwardAnimation() {
		return ANIMATION_UPWARD;
	}

	@Override
	public ResourceLocation getTexture(FaithPlateBlockEntity entity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getEmissiveTexture(FaithPlateBlockEntity entity) {
		return TEXTURE_E;
	}
}
