package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.FaithPlateBlock;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import io.github.gaming32.jsonentityanimation.api.JsonAnimator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FaithPlateModel extends EntityLikeBlockEntityModel<FaithPlateBlockEntity> {
    public static final ResourceLocation TEXTURE = id("textures/block/faith_plate.png");
    public static final ResourceLocation TEXTURE_E = id("textures/block/faith_plate_e.png");

    public static final ResourceLocation ANIMATION_FORWARD = id("faith_plate/forward");
    public static final ResourceLocation ANIMATION_UPWARD = id("faith_plate/upward");

    private final ModelPart root;
    private final JsonAnimator animator = new JsonAnimator(this);

    public FaithPlateModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition group = modelPartData.addOrReplaceChild("group", CubeListBuilder.create(), PartPose.offset(-8.0F, 2.0F, 11.5F));

        group.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(20, 49).addBox(-4.0F, -0.5F, -16.0F, 8.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(2, 42).addBox(0.0F, 0.5F, -11.0F, 0.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 4.5F, -2.5F));

        PartDefinition bone2 = group.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(8.0F, 7.2675F, -6.8446F));

        bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(14, 49).mirror().addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition bone = group.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(50, 50).addBox(-2.25F, -1.5F, -1.3333F, 0.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(50, 53).addBox(2.25F, -1.5F, -1.3333F, 0.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.25F, -15.4167F));

        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 61).addBox(-4.0F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1667F, 0.7854F, 0.0F, 0.0F));

        PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(36, 6).addBox(-4.0F, -16.0F, -7.0F, 0.0F, 7.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(22, 29).addBox(4.0F, -17.0F, -8.0F, 4.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 28).addBox(-4.0F, -17.0F, -8.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 30).addBox(-8.0F, -17.0F, -8.0F, 4.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26).addBox(-4.0F, -17.0F, 7.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        base.addOrReplaceChild("cubeinverted_r1", CubeListBuilder.create().texOffs(36, 6).addBox(-4.0F, -16.0F, -7.0F, 0.0F, 7.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(28, 15).addBox(-4.0F, -9.0F, -7.0F, 8.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        base.addOrReplaceChild("cubeinverted_r2", CubeListBuilder.create().texOffs(42, 14).addBox(0.0F, -3.5F, -4.0F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.5F, 7.0F, 0.0F, 1.5708F, 0.0F));

        base.addOrReplaceChild("cubeinverted_r3", CubeListBuilder.create().texOffs(42, 14).addBox(-7.0F, -16.0F, -4.0F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(BlockEntityWrapperEntity<FaithPlateBlockEntity> entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        root().getAllParts().forEach(ModelPart::resetPose);
        root.setRotation((float)Math.toRadians(headPitch), (float)Math.toRadians(headYaw), 0);
        if (entity.getBlockEntity().pitch == 90f) {
            final float radians = (float)Math.toRadians(entity.getBlockEntity().yaw);
            root.setPos(-(float)Math.sin(radians) * 16f, 16f, -(float)Math.cos(radians) * 16f);
        } else if (entity.getBlockEntity().pitch == -180f) {
            root.setPos(0f, 32f, 0f);
        }
        final FaithPlateBlockEntity faithPlate = entity.getBlockEntity();
        final boolean upward =
            faithPlate.getBlockState().getValue(FaithPlateBlock.FACING).getAxis().isVertical() &&
                faithPlate.getVelX() == 0 && faithPlate.getVelZ() == 0 && faithPlate.getVelY() != 0;
        animator.animate(faithPlate.flingState, upward ? getUpwardAnimation() : getForwardAnimation(), animationProgress);
    }

    protected ResourceLocation getForwardAnimation() {
        return ANIMATION_FORWARD;
    }

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
