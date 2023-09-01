package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.RocketTurretBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.gaming32.jsonentityanimation.api.JsonAnimator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class RocketTurretModel extends EntityLikeBlockEntityModel<RocketTurretBlockEntity> {
	public static final ResourceLocation TEXTURE_ACTIVE = id("textures/block/rocket_turret_active.png");
	public static final ResourceLocation TEXTURE_ACTIVE_E = id("textures/block/rocket_turret_active_e.png");
	public static final ResourceLocation TEXTURE_INACTIVE = id("textures/block/rocket_turret_inactive.png");

	public static final ResourceLocation TEXTURE_LOCK_ON = id("textures/block/rocket_turret_lock_on.png");
	public static final ResourceLocation TEXTURE_LOCK_ON_E = id("textures/block/rocket_turret_lock_on_e.png");

	public static final ResourceLocation TEXTURE_FIRING = id("textures/block/rocket_turret_firing.png");
	public static final ResourceLocation TEXTURE_FIRING_E = id("textures/block/rocket_turret_firing_e.png");

	public static final ResourceLocation ANIMATION_ACTIVATE = id("rocket_turret/activate");
	public static final ResourceLocation ANIMATION_DEACTIVATE = id("rocket_turret/deactivate");
	public static final ResourceLocation ANIMATION_SHOOT = id("rocket_turret/shoot");

	private final ModelPart root, turret, chassis, neck;
	private final JsonAnimator animator = new JsonAnimator(this);

	public RocketTurretModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
		turret = root.getChild("turret");
		chassis = turret.getChild("chassis");
		neck = chassis.getChild("segment_1").getChild("segment_2").getChild("segment_3").getChild("neck");
	}

	@SuppressWarnings("checkstyle:LocalVariableName")
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition turret = modelPartData.addOrReplaceChild("turret", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

		PartDefinition chassis = turret.addOrReplaceChild("chassis", CubeListBuilder.create().texOffs(26, 32).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

		chassis.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 21).addBox(-3.5F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 1.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition segment_1 = chassis.addOrReplaceChild("segment_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0429F, 1.9571F, 0.3491F, 0.0F, 0.0F));

		segment_1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, -1.5F, -2.0F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.9874F, 2.841F, 0.7854F, 0.0F, 0.0F));

		segment_1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 38).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5126F, -0.341F, 0.7854F, 0.0F, 0.0F));

		PartDefinition segment_2 = segment_1.addOrReplaceChild("segment_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.6137F, 3.4751F, 0.1309F, 0.0F, 0.0F));

		segment_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(42, 21).addBox(-3.5F, -0.8826F, -1.0891F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4742F, 3.3266F, -1.1781F, 0.0F, 0.0F));

		segment_2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 42).addBox(-3.0F, 0.75F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4951F, 1.4872F, 0.0436F, 0.0F, 0.0F));

		PartDefinition segment_3 = segment_2.addOrReplaceChild("segment_3", CubeListBuilder.create(), PartPose.offset(0.0083F, -0.4755F, 3.1777F));

		segment_3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(42, 21).addBox(-3.5F, -0.7298F, -1.2949F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0083F, -3.1352F, 1.5022F, -2.7925F, 0.0F, 0.0F));

		segment_3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(30, 42).addBox(-3.0F, -0.354F, 0.02F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0083F, 0.0013F, 0.1489F, 1.1781F, 0.0F, 0.0F));

		PartDefinition neck = segment_3.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0167F, -3.6138F, 1.5099F, -0.5105F, 0.0F, 0.0F));

		neck.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(8, 16).addBox(-0.5F, -2.5728F, -0.4052F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8232F, -2.5263F, -1.8326F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 52).addBox(-2.25F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(16, 0).addBox(-3.75F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, -0.7007F, -5.6938F));

		PartDefinition hatch = head.addOrReplaceChild("hatch", CubeListBuilder.create().texOffs(37, 7).addBox(0.0F, -2.75F, -3.75F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(44, 41).addBox(-3.0694F, -2.2721F, -2.7033F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.25F, -0.75F, 0.25F));

		hatch.addOrReplaceChild("missile_r1", CubeListBuilder.create().texOffs(38, 54).mirror().addBox(5.5374F, -8.5667F, -3.0513F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(38, 54).mirror().addBox(8.2947F, -9.9106F, -3.0208F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(38, 54).mirror().addBox(6.527F, -8.1428F, -3.0208F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(38, 54).mirror().addBox(7.2338F, -7.436F, -2.9903F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(38, 54).mirror().addBox(7.6577F, -6.4464F, -2.9597F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-12.25F, 1.0F, -0.25F, 0.0F, 0.0F, 0.7854F));

		hatch.addOrReplaceChild("missile_r2", CubeListBuilder.create().texOffs(38, 54).addBox(-1.4639F, -3.4932F, -3.0513F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(38, 54).addBox(-0.12F, -0.7359F, -3.0208F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(38, 54).addBox(-1.8878F, -2.5036F, -3.0208F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(38, 54).addBox(-2.5946F, -1.7968F, -2.9903F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(38, 54).addBox(-3.5842F, -1.3729F, -2.9597F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, 1.0F, -0.25F, 0.0F, 0.0F, -0.7854F));

		PartDefinition gun = hatch.addOrReplaceChild("gun", CubeListBuilder.create(), PartPose.offset(0.95F, 1.4845F, 0.265F));

		gun.addOrReplaceChild("gun_r1", CubeListBuilder.create().texOffs(43, 25).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition barrel = gun.addOrReplaceChild("barrel", CubeListBuilder.create(), PartPose.offset(0.0F, 0.1952F, -4.9748F));

		barrel.addOrReplaceChild("barrel_r1", CubeListBuilder.create().texOffs(52, 27).addBox(-1.1574F, -0.1961F, -6.9597F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -0.6797F, 4.4597F, 0.0F, 0.0F, -0.7854F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void setupAnim(BlockEntityWrapperEntity<RocketTurretBlockEntity> entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		chassis.setRotation(0, (float)Math.toRadians(headYaw - 90), 0);
		neck.setRotation((float)Math.toRadians(headPitch - 30), (float)Math.toRadians(-0.5105f), 0);
		animator.animate(entity.getBlockEntity().activatingAnimation, ANIMATION_ACTIVATE, animationProgress);
		animator.animate(entity.getBlockEntity().deactivatingAnimation, ANIMATION_DEACTIVATE, animationProgress);
		animator.animate(entity.getBlockEntity().shootAnimation, ANIMATION_SHOOT, animationProgress);
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		turret.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ResourceLocation getTexture(RocketTurretBlockEntity entity) {
		if (entity.deactivatingAnimation.isStarted() && entity.deactivatingAnimation.getAccumulatedTime() >= 2000) {
			return TEXTURE_INACTIVE;
		}
		return switch (entity.getState()) {
			case SEARCHING -> TEXTURE_ACTIVE;
			case LOCKED -> TEXTURE_LOCK_ON;
			case FIRING -> TEXTURE_FIRING;
		};
	}

	@Override
	public ResourceLocation getEmissiveTexture(RocketTurretBlockEntity entity) {
		if (entity.deactivatingAnimation.isStarted() && entity.deactivatingAnimation.getAccumulatedTime() >= 2000) {
			return null;
		}
		return switch (entity.getState()) {
			case SEARCHING -> TEXTURE_ACTIVE_E;
			case LOCKED -> TEXTURE_LOCK_ON_E;
			case FIRING -> TEXTURE_FIRING_E;
		};
	}
}
