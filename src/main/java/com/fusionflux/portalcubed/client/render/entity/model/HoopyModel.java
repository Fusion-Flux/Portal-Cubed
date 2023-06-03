// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.HoopyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class HoopyModel extends FizzleableModel<HoopyEntity> {
    public static final ModelLayerLocation HOOPY_LAYER = new ModelLayerLocation(id("hoopy"), "main");
    @SuppressWarnings("checkstyle:MemberName")
    private final ModelPart bb_main;

    public HoopyModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -1.0F, -13.0F, 26.0F, 1.0F, 26.0F, new CubeDeformation(0.0F))
            .texOffs(0, 27).addBox(-12.0F, -1.0F, -12.0F, 24.0F, 1.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void renderFizzled(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
