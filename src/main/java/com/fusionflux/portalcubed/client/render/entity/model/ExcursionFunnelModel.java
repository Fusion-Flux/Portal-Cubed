package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.beams.EmittedEntity;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class ExcursionFunnelModel extends Model {
    public static final Set<Direction> VISIBLE_MIDDLE = Set.of(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    public static final Set<Direction> VISIBLE_END = Set.of(Direction.values());
    public static final ResourceLocation TEXTURE = PortalCubed.id("textures/animated_entity/excursion_funnel_beam_forward.png");
    public static final ResourceLocation REVERSED_TEXTURE = PortalCubed.id("textures/animated_entity/excursion_funnel_beam_reversed.png");
    private ModelPart part;

    public ExcursionFunnelModel(ExcursionFunnelEntity entity) {
        super(RenderType::entityTranslucent);
        entity.modelUpdater = this::rebuildGeometry;
        this.rebuildGeometry(entity);
    }

    public void rebuildGeometry(EmittedEntity entity) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // this hurts my soul, but I don't know if improving it is possible
        float length = entity.getLength();
        float tinyOffset = (entity.getId() % 20) / 10_000f; // avoids Z fighting when crossing
        for (int block = 0; block < length; block++) {
            float sectionLength = Math.min(length - block, 1);
            boolean last = block + 1 >= length;
            if (last) sectionLength -= 0.001; // avoid z fighting at end
            root.addOrReplaceChild(
                "cube_" + block,
                CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(
                        -15 + tinyOffset, block * 16 + tinyOffset, -15 + tinyOffset,
                        30, sectionLength * 16, 30,
                        last ? VISIBLE_END : VISIBLE_MIDDLE
                    ),
                PartPose.ZERO
            );
        }

        this.part = LayerDefinition.create(mesh, 120, 1472).bakeRoot();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
