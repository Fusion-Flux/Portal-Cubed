// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.TurretEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.fusionflux.portalcubed.PortalCubed.id;
import static com.fusionflux.portalcubed.entity.TurretEntity.MODEL_SCALE;

public class TurretModel extends FizzleableModel<TurretEntity> {
    public static final EntityModelLayer TURRET_MAIN_LAYER = new EntityModelLayer(id("turret"), "main");

    public static final Identifier DEFAULT_TEXTURE = id("textures/entity/default_turret.png");

    private final ModelPart turret;

    public TurretModel(ModelPart root) {
        this.turret = root.getChild("turret");
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData turret = modelPartData.addChild("turret", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData body = turret.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData center = body.addChild("center", ModelPartBuilder.create().uv(0, 0).cuboid(-2.75F, -9.875F, -4.5F, 6.0F, 16.0F, 8.0F, new Dilation(0.02F))
            .uv(29, 41).cuboid(-2.75F, -2.375F, -0.525F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
            .uv(0, 42).cuboid(-1.75F, -7.85F, -4.5F, 4.0F, 14.0F, 8.0F, new Dilation(0.0F))
            .uv(-6, 0).cuboid(-2.75F, -7.925F, -4.5F, 6.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.25F, -16.125F, 0.5F));

        ModelPartData antenna = center.addChild("antenna", ModelPartBuilder.create(), ModelTransform.pivot(-0.9F, -6.125F, 1.5F));

        antenna.addChild("cube_r1", ModelPartBuilder.create().uv(28, 0).cuboid(0.0F, -3.55F, -2.5F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.1781F, 0.0F));

        ModelPartData crown = center.addChild("crown", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        crown.addChild("cube_r2", ModelPartBuilder.create().uv(37, 42).mirrored().cuboid(-2.5F, -2.5F, -3.5F, 7.0F, 5.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
            .uv(37, 42).cuboid(-2.5F, -2.5F, 3.5F, 7.0F, 5.0F, 0.0F, new Dilation(0.0F))
            .uv(37, 35).mirrored().cuboid(4.5F, -2.5F, -3.5F, 0.0F, 5.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
            .uv(37, 35).mirrored().cuboid(-2.5F, -2.5F, -3.5F, 0.0F, 5.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.5F, -12.5F, -0.5F, -0.0289F, -0.2163F, 0.134F));

        ModelPartData eye = center.addChild("eye", ModelPartBuilder.create(), ModelTransform.of(0.25F, -0.875F, -4.425F, 0.0F, 0.0F, -0.7854F));

        eye.addChild("cube_r3", ModelPartBuilder.create().uv(22, 0).cuboid(-1.5177F, -1.5177F, 0.0F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.025F, 0.0F, 0.0F, -1.5708F));

        ModelPartData left_hatch = body.addChild("left_hatch", ModelPartBuilder.create().uv(29, 42).mirrored().cuboid(-3.405F, -1.93F, -0.1362F, 5.0F, 2.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(1.365F, -16.585F, 0.1213F));

        ModelPartData left_pivot = left_hatch.addChild("left_pivot", ModelPartBuilder.create().uv(44, 40).mirrored().cuboid(2.3333F, -7.75F, -4.1667F, 2.0F, 16.0F, 8.0F, new Dilation(0.01F)).mirrored(false)
            .uv(48, 12).mirrored().cuboid(0.0333F, -3.5F, -2.1667F, 3.0F, 6.0F, 5.0F, new Dilation(0.01F)).mirrored(false)
            .uv(24, 40).mirrored().cuboid(4.1333F, -7.75F, -4.1667F, 0.0F, 16.0F, 8.0F, new Dilation(-0.01F)).mirrored(false), ModelTransform.pivot(-2.6983F, 0.31F, 0.0454F));

        ModelPartData left_barrels = left_pivot.addChild("left_barrels", ModelPartBuilder.create().uv(58, 23).mirrored().cuboid(-0.75F, -1.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.01F)).mirrored(false)
            .uv(58, 23).mirrored().cuboid(-0.75F, 1.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.pivot(1.7833F, -1.0F, -2.9167F));

        ModelPartData top_left_flash = left_barrels.addChild("top_left_flash", ModelPartBuilder.create(), ModelTransform.pivot(-0.25F, -0.5F, 5.0F));

        top_left_flash.addChild("cube_r4", ModelPartBuilder.create().uv(22, -1).cuboid(-0.075F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.7854F));

        top_left_flash.addChild("cube_r5", ModelPartBuilder.create().uv(22, -1).cuboid(-0.075F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

        ModelPartData bottom_left_flash = left_barrels.addChild("bottom_left_flash", ModelPartBuilder.create(), ModelTransform.pivot(-0.325F, 2.0F, 5.0F));

        bottom_left_flash.addChild("cube_r6", ModelPartBuilder.create().uv(22, -1).cuboid(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.7854F));

        bottom_left_flash.addChild("cube_r7", ModelPartBuilder.create().uv(22, -1).cuboid(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

        ModelPartData right_hatch = body.addChild("right_hatch", ModelPartBuilder.create().uv(29, 42).cuboid(-1.595F, -1.93F, -0.1362F, 5.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.365F, -16.585F, 0.1213F));

        ModelPartData right_pivot = right_hatch.addChild("right_pivot", ModelPartBuilder.create().uv(48, 12).cuboid(-3.0333F, -3.5F, -2.1667F, 3.0F, 6.0F, 5.0F, new Dilation(0.01F))
            .uv(24, 40).cuboid(-4.1333F, -7.75F, -4.1667F, 0.0F, 16.0F, 8.0F, new Dilation(-0.01F))
            .uv(20, 16).cuboid(-4.3333F, -7.75F, -4.1667F, 2.0F, 16.0F, 8.0F, new Dilation(0.01F)), ModelTransform.pivot(2.6983F, 0.31F, 0.0454F));

        ModelPartData right_barrels = right_pivot.addChild("right_barrels", ModelPartBuilder.create().uv(58, 23).cuboid(-0.25F, -1.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.01F))
            .uv(58, 23).cuboid(-0.25F, 1.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.01F)), ModelTransform.pivot(-1.7833F, -1.0F, -2.9167F));

        ModelPartData bottom_right_flash = right_barrels.addChild("bottom_right_flash", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, 2.0F, 5.0F));

        bottom_right_flash.addChild("cube_r8", ModelPartBuilder.create().uv(22, -1).mirrored().cuboid(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.7854F));

        bottom_right_flash.addChild("cube_r9", ModelPartBuilder.create().uv(22, -1).mirrored().cuboid(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData top_right_flash = right_barrels.addChild("top_right_flash", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, -0.5F, 5.0F));

        top_right_flash.addChild("cube_r10", ModelPartBuilder.create().uv(22, -1).mirrored().cuboid(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.7854F));

        top_right_flash.addChild("cube_r11", ModelPartBuilder.create().uv(22, -1).mirrored().cuboid(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData legs = turret.addChild("legs", ModelPartBuilder.create().uv(0, 15).cuboid(0.0F, -7.6667F, 0.7663F, 0.0F, 14.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.3333F, 0.7337F));

        legs.addChild("cube_r12", ModelPartBuilder.create().uv(42, -11).cuboid(0.0F, -11.0F, 0.0F, 0.0F, 9.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 8.3333F, -0.4837F, 0.0F, -2.7227F, 0.0F));

        legs.addChild("cube_r13", ModelPartBuilder.create().uv(42, -11).mirrored().cuboid(0.0F, -11.0F, 0.0F, 0.0F, 9.0F, 11.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.25F, 8.3333F, -0.4837F, 0.0F, 2.7227F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void renderFizzled(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.push();
        matrixStack.translate(0, 24 / 16f * (1 - MODEL_SCALE), 0);
        matrixStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        turret.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        matrixStack.pop();
    }
}
