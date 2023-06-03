// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.LilPineappleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class LilPineappleModel extends FizzleableModel<LilPineappleEntity> {
    public static final ModelLayerLocation LIL_PINEAPPLE = new ModelLayerLocation(id("lil_pineapple"), "main");
    @SuppressWarnings("checkstyle:MemberName")
    private final ModelPart bb_main;

    public LilPineappleModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        bb_main.addOrReplaceChild("bb_main_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition bd = bb_main.addOrReplaceChild("bd", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.5F, -7.0F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 0.5F, 2.5F));

        bd.addOrReplaceChild("bd_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3F, -9.8F, -2.5F, 0.0F, 0.0F, -0.0873F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void renderFizzled(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
