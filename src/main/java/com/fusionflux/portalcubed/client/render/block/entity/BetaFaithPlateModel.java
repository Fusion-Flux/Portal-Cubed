package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BetaFaithPlateModel extends FaithPlateModel {
    public static final Identifier TEXTURE = id("textures/block/faith_plate.png");
    public static final Identifier TEXTURE_E = id("textures/block/faith_plate_e.png");

    public static final Identifier ANIMATION_FORWARD = id("beta_faith_plate/forward");
    public static final Identifier ANIMATION_UPWARD = id("beta_faith_plate/upward");

    public BetaFaithPlateModel(ModelPart root) {
        super(root);
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 15.3696F, -2.787F));

        bone.addChild("cube_r1", ModelPartBuilder.create().uv(42, 5).cuboid(-0.5F, -2.5F, -3.5F, 2.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -1.4946F, 5.037F, 0.3927F, 0.0F, 0.0F));

        bone.addChild("cube_r2", ModelPartBuilder.create().uv(0, 15).cuboid(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.6196F, 0.037F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bone2 = bone.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.6712F, 7.4931F));

        bone2.addChild("cube_r3", ModelPartBuilder.create().uv(44, 7).cuboid(-0.5F, -2.5F, -1.5F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -0.5484F, -4.4561F, -0.3927F, 0.0F, 0.0F));

        bone2.addChild("cube_r4", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -0.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0516F, -1.4561F, 0.7854F, 0.0F, 0.0F));

        bone2.addChild("bone3", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0167F, -2.3333F, -7.0F, 14.0F, 1.0F, 14.0F, new Dilation(0.0F))
            .uv(30, 18).cuboid(-1.1167F, -1.3333F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
            .uv(30, 18).cuboid(1.1333F, -1.3333F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0167F, -2.3651F, -4.7061F));

        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(22, 15).cuboid(1.0F, 11.5F, -15.0F, 14.0F, 0.0F, 14.0F, new Dilation(0.0F))
            .uv(36, 4).cuboid(15.0F, 0.5F, -15.0F, 0.0F, 11.0F, 14.0F, new Dilation(0.0F))
            .uv(30, 32).cuboid(14.99F, -0.51F, -16.01F, 1.0F, 1.0F, 16.0F, new Dilation(0.0F))
            .uv(0, 26).cuboid(0.99F, -0.51F, -1.01F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
            .uv(12, 31).cuboid(-0.01F, -0.51F, -16.01F, 1.0F, 1.0F, 16.0F, new Dilation(0.0F))
            .uv(0, 28).cuboid(0.99F, -0.51F, -16.01F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 7.5F, 8.0F));

        base.addChild("cubeinverted_r1", ModelPartBuilder.create().uv(36, 4).cuboid(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 16.5F, -8.0F, 0.0F, -1.5708F, 0.0F));

        base.addChild("cubeinverted_r2", ModelPartBuilder.create().uv(36, 4).cuboid(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 16.5F, -8.0F, 0.0F, 3.1416F, 0.0F));

        base.addChild("cubeinverted_r3", ModelPartBuilder.create().uv(36, 4).cuboid(7.0F, -16.0F, -7.0F, 0.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 16.5F, -8.0F, 0.0F, 1.5708F, 0.0F));

        modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(21, 16).cuboid(-2.65F, -10.75F, -4.75F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F))
            .uv(21, 16).cuboid(2.65F, -10.75F, -4.75F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected Identifier getForwardAnimation() {
        return ANIMATION_FORWARD;
    }

    @Override
    protected Identifier getUpwardAnimation() {
        return ANIMATION_UPWARD;
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
