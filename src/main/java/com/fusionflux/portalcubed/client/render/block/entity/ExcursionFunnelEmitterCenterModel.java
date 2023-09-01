package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class ExcursionFunnelEmitterCenterModel extends Model {
	public static final ResourceLocation TEXTURE = PortalCubed.id("textures/block/excursion_funnel_emitter_center.png");
	public static final ResourceLocation TEXTURE_REVERSED = PortalCubed.id("textures/block/excursion_funnel_emitter_center_reversed.png");
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(TEXTURE, "main");
	public static final ModelLayerLocation LAYER_REVERSED = new ModelLayerLocation(TEXTURE_REVERSED, "main");

	@SuppressWarnings("checkstyle:MemberName")
	private final ModelPart you_spin_me_right_round;
	@SuppressWarnings("checkstyle:MemberName")
	private final ModelPart bb_main;

	public final ResourceLocation texture;

	public ExcursionFunnelEmitterCenterModel(ModelPart root, ResourceLocation texture) {
		super(RenderType::entityCutout);
		this.you_spin_me_right_round = root.getChild("you_spin_me_right_round");
		this.bb_main = root.getChild("bb_main");
		this.texture = texture;
	}

	public RenderType getRenderType() {
		return super.renderType(texture);
	}

	public static ExcursionFunnelEmitterCenterModel forward(Context ctx) {
		return new ExcursionFunnelEmitterCenterModel(ctx.bakeLayer(LAYER), TEXTURE);
	}

	public static ExcursionFunnelEmitterCenterModel reversed(Context ctx) {
		return new ExcursionFunnelEmitterCenterModel(ctx.bakeLayer(LAYER_REVERSED), TEXTURE_REVERSED);
	}

	@SuppressWarnings("checkstyle:LocalVariableName")
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition you_spin_me_right_round = partdefinition.addOrReplaceChild("you_spin_me_right_round", CubeListBuilder.create(), PartPose.offset(0.0F, 20.75F, 0.0F));

		you_spin_me_right_round.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.75F, 0.0F, -3.5F, 8.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 27).addBox(0.0F, -2.7875F, 1.4257F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
			.texOffs(0, 26).addBox(0.0F, -2.7875F, -1.4027F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(5, 18).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
			.texOffs(5, 18).addBox(-2.5F, -1.0F, 2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0429F, -0.8375F, 0.0125F, 0.0F, 2.3562F, 0.0F));

		bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 18).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
			.texOffs(0, 18).addBox(-2.5F, -1.0F, 2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
			.texOffs(0, 13).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0429F, -0.8375F, 0.0125F, 0.0F, 0.7854F, 0.0F));

		bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(0, 6).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.7875F, 0.0125F, 0.0F, 0.7854F, 0.0F));

		bb_main.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7875F, 0.0125F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public void rotate(float degrees) {
		you_spin_me_right_round.setRotation(0, degrees, 0);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		you_spin_me_right_round.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
