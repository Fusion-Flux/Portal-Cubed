// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.TurretEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;
import static com.fusionflux.portalcubed.entity.TurretEntity.MODEL_SCALE;

public class TurretModel extends FizzleableModel<TurretEntity> {
    public static final ModelLayerLocation TURRET_MAIN_LAYER = new ModelLayerLocation(id("turret"), "main");

    public static final ResourceLocation DEFAULT_TEXTURE = id("textures/entity/default_turret.png");

    private final ModelPart turret;

    public TurretModel(ModelPart root) {
        this.turret = root.getChild("turret");
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition turret = modelPartData.addOrReplaceChild("turret", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = turret.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition center = body.addOrReplaceChild("center", CubeListBuilder.create().texOffs(0, 0).addBox(-2.75F, -9.875F, -4.5F, 6.0F, 16.0F, 8.0F, new CubeDeformation(0.02F))
            .texOffs(29, 41).addBox(-2.75F, -2.375F, -0.525F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 42).addBox(-1.75F, -7.85F, -4.5F, 4.0F, 14.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(-6, 0).addBox(-2.75F, -7.925F, -4.5F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, -16.125F, 0.5F));

        PartDefinition antenna = center.addOrReplaceChild("antenna", CubeListBuilder.create(), PartPose.offset(-0.9F, -6.125F, 1.5F));

        antenna.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 0).addBox(0.0F, -3.55F, -2.5F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.1781F, 0.0F));

        PartDefinition crown = center.addOrReplaceChild("crown", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        crown.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(37, 42).mirror().addBox(-2.5F, -2.5F, -3.5F, 7.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(37, 42).addBox(-2.5F, -2.5F, 3.5F, 7.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(37, 35).mirror().addBox(4.5F, -2.5F, -3.5F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(37, 35).mirror().addBox(-2.5F, -2.5F, -3.5F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -12.5F, -0.5F, -0.0289F, -0.2163F, 0.134F));

        PartDefinition eye = center.addOrReplaceChild("eye", CubeListBuilder.create(), PartPose.offsetAndRotation(0.25F, -0.875F, -4.425F, 0.0F, 0.0F, -0.7854F));

        eye.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(22, 0).addBox(-1.5177F, -1.5177F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.025F, 0.0F, 0.0F, -1.5708F));

        PartDefinition left_hatch = body.addOrReplaceChild("left_hatch", CubeListBuilder.create().texOffs(29, 42).mirror().addBox(-3.405F, -1.93F, -0.1362F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.365F, -16.585F, 0.1213F));

        PartDefinition left_pivot = left_hatch.addOrReplaceChild("left_pivot", CubeListBuilder.create().texOffs(44, 40).mirror().addBox(2.3333F, -7.75F, -4.1667F, 2.0F, 16.0F, 8.0F, new CubeDeformation(0.01F)).mirror(false)
            .texOffs(48, 12).mirror().addBox(0.0333F, -3.5F, -2.1667F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.01F)).mirror(false)
            .texOffs(24, 40).mirror().addBox(4.1333F, -7.75F, -4.1667F, 0.0F, 16.0F, 8.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-2.6983F, 0.31F, 0.0454F));

        PartDefinition left_barrels = left_pivot.addOrReplaceChild("left_barrels", CubeListBuilder.create().texOffs(58, 23).mirror().addBox(-0.75F, -1.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)).mirror(false)
            .texOffs(58, 23).mirror().addBox(-0.75F, 1.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(1.7833F, -1.0F, -2.9167F));

        PartDefinition top_left_flash = left_barrels.addOrReplaceChild("top_left_flash", CubeListBuilder.create(), PartPose.offset(-0.25F, -0.5F, 5.0F));

        top_left_flash.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(22, -1).addBox(-0.075F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.7854F));

        top_left_flash.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(22, -1).addBox(-0.075F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

        PartDefinition bottom_left_flash = left_barrels.addOrReplaceChild("bottom_left_flash", CubeListBuilder.create(), PartPose.offset(-0.325F, 2.0F, 5.0F));

        bottom_left_flash.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(22, -1).addBox(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.7854F));

        bottom_left_flash.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(22, -1).addBox(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

        PartDefinition right_hatch = body.addOrReplaceChild("right_hatch", CubeListBuilder.create().texOffs(29, 42).addBox(-1.595F, -1.93F, -0.1362F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.365F, -16.585F, 0.1213F));

        PartDefinition right_pivot = right_hatch.addOrReplaceChild("right_pivot", CubeListBuilder.create().texOffs(48, 12).addBox(-3.0333F, -3.5F, -2.1667F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.01F))
            .texOffs(24, 40).addBox(-4.1333F, -7.75F, -4.1667F, 0.0F, 16.0F, 8.0F, new CubeDeformation(-0.01F))
            .texOffs(20, 16).addBox(-4.3333F, -7.75F, -4.1667F, 2.0F, 16.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(2.6983F, 0.31F, 0.0454F));

        PartDefinition right_barrels = right_pivot.addOrReplaceChild("right_barrels", CubeListBuilder.create().texOffs(58, 23).addBox(-0.25F, -1.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.01F))
            .texOffs(58, 23).addBox(-0.25F, 1.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(-1.7833F, -1.0F, -2.9167F));

        PartDefinition bottom_right_flash = right_barrels.addOrReplaceChild("bottom_right_flash", CubeListBuilder.create(), PartPose.offset(0.25F, 2.0F, 5.0F));

        bottom_right_flash.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(22, -1).mirror().addBox(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.7854F));

        bottom_right_flash.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(22, -1).mirror().addBox(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

        PartDefinition top_right_flash = right_barrels.addOrReplaceChild("top_right_flash", CubeListBuilder.create(), PartPose.offset(0.25F, -0.5F, 5.0F));

        top_right_flash.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(22, -1).mirror().addBox(0.0F, -1.5F, -2.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.7854F));

        top_right_flash.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(22, -1).mirror().addBox(0.0F, -1.5F, -3.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

        PartDefinition legs = turret.addOrReplaceChild("legs", CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, -7.6667F, 0.7663F, 0.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.3333F, 0.7337F));

        legs.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(42, -11).addBox(0.0F, -11.0F, 0.0F, 0.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 8.3333F, -0.4837F, 0.0F, -2.7227F, 0.0F));

        legs.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(42, -11).mirror().addBox(0.0F, -11.0F, 0.0F, 0.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.25F, 8.3333F, -0.4837F, 0.0F, 2.7227F, 0.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void renderFizzled(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();
        matrixStack.translate(0, 24 / 16f * (1 - MODEL_SCALE), 0);
        matrixStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        turret.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        matrixStack.popPose();
    }
}
