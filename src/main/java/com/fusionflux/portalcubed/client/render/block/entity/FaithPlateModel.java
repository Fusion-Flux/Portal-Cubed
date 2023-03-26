package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.animation.Animation;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FaithPlateModel extends EntityLikeBlockEntityModel<FaithPlateBlockEntity> {
    public static final Identifier TEXTURE = id("textures/block/faith_plate.png");
    public static final Identifier TEXTURE_E = id("textures/block/faith_plate_e.png");

    private final ModelPart root;

    public FaithPlateModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.root = root;
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData group = modelPartData.addChild("group", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, 2.0F, 11.5F));

        group.addChild("bone3", ModelPartBuilder.create().uv(20, 49).cuboid(-4.0F, -0.5F, -16.0F, 8.0F, 1.0F, 14.0F, new Dilation(0.0F))
            .uv(2, 42).cuboid(0.0F, 0.5F, -11.0F, 0.0F, 4.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 4.5F, -2.5F));

        ModelPartData bone2 = group.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, 7.2675F, -6.8446F));

        bone2.addChild("cube_r1", ModelPartBuilder.create().uv(14, 49).mirrored().cuboid(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bone = group.addChild("bone", ModelPartBuilder.create().uv(50, 50).cuboid(-2.25F, -1.5F, -1.3333F, 0.0F, 3.0F, 7.0F, new Dilation(0.0F))
            .uv(50, 53).cuboid(2.25F, -1.5F, -1.3333F, 0.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 8.25F, -15.4167F));

        bone.addChild("cube_r2", ModelPartBuilder.create().uv(16, 61).cuboid(-4.0F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.1667F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 30).cuboid(-8.0F, -17.0F, -8.0F, 4.0F, 1.0F, 16.0F, new Dilation(0.0F))
            .uv(20, 18).cuboid(-4.0F, -17.0F, -8.0F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F))
            .uv(21, 16).cuboid(-4.0F, -17.0F, 7.0F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F))
            .uv(22, 29).cuboid(4.0F, -17.0F, -8.0F, 4.0F, 1.0F, 16.0F, new Dilation(0.0F))
            .uv(0, 11).cuboid(-4.0F, -16.0F, -7.0F, 0.0F, 7.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        bb_main.addChild("cubeinverted_r1", ModelPartBuilder.create().uv(-8, 25).cuboid(-7.0F, -9.0F, -4.0F, 14.0F, 0.0F, 8.0F, new Dilation(0.0F))
            .uv(6, 17).cuboid(-7.0F, -16.0F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        bb_main.addChild("cubeinverted_r2", ModelPartBuilder.create().uv(6, 17).cuboid(0.0F, -3.5F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.5F, 7.0F, 0.0F, 1.5708F, 0.0F));

        bb_main.addChild("cubeinverted_r3", ModelPartBuilder.create().uv(0, 11).cuboid(-4.0F, -16.0F, -7.0F, 0.0F, 7.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(BlockEntityWrapperEntity<FaithPlateBlockEntity> entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        getPart().traverse().forEach(ModelPart::resetTransform);
        root.setAngles((float)Math.toRadians(headPitch), (float)Math.toRadians(headYaw), 0);
        if (entity.getBlockEntity().pitch != 0) {
            final float radians = (float)Math.toRadians(entity.getBlockEntity().yaw);
            root.setPivot(-(float)Math.sin(radians) * 16, 16f, -(float)Math.cos(radians) * 16);
        }
        m_rrbozhsc(
            entity.getBlockEntity().flingState,
            entity.getBlockEntity().pitch != 0 ? getForwardAnimation() : getUpwardAnimation(),
            animationProgress
        );
    }

    protected Animation getForwardAnimation() {
        return FaithPlateAnimations.FP_FORWARD;
    }

    protected Animation getUpwardAnimation() {
        return FaithPlateAnimations.FP_UPWARD;
    }

    @Override
    public Identifier getTexture(FaithPlateBlockEntity entity) {
        return TEXTURE;
    }

    @Override
    public Identifier getEmissiveTexture(FaithPlateBlockEntity entity) {
        return TEXTURE_E;
    }
}
