package com.fusionflux.portalcubed.client.render.block.entity;

import java.util.List;

import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelEmitterBlockEntity;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.blocks.funnel.TwoByTwoFacingMultiblockBlock;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class ExcursionFunnelEmitterBlockEntityRenderer implements BlockEntityRenderer<ExcursionFunnelEmitterBlockEntity> {
    public static final List<Property<?>> REQUIRED_PROPERTIES = List.of(
            TwoByTwoFacingMultiblockBlock.QUADRANT,
            TwoByTwoFacingMultiblockBlock.FACING,
            ExcursionFunnelEmitterBlock.MODE
    );

    protected final ExcursionFunnelEmitterCenterModel forwardCenterModel, reversedCenterModel;

    public ExcursionFunnelEmitterBlockEntityRenderer(Context ctx) {
        this.forwardCenterModel = ExcursionFunnelEmitterCenterModel.forward(ctx);
        this.reversedCenterModel = ExcursionFunnelEmitterCenterModel.reversed(ctx);
    }

    @Override
    public void render(ExcursionFunnelEmitterBlockEntity be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        Mode mode = state.getValue(ExcursionFunnelEmitterBlock.MODE);
        Direction facing = state.getValue(TwoByTwoFacingMultiblockBlock.FACING);
        ExcursionFunnelEmitterCenterModel centerModel = mode.isReversed ? reversedCenterModel : forwardCenterModel;

        float tickTime = getTickTime(be.getLevel(), partialTicks, mode);
        float rotation = tickTime / (mode.isReversed ? 6 : -6);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(facing.getRotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(-0.5, -1, -0.5);
        if (facing.getAxis().isVertical())
            poseStack.translate(1, 0, 1);

        centerModel.rotate(rotation);
        VertexConsumer consumer = buffers.getBuffer(centerModel.getRenderType());
        centerModel.renderToBuffer(poseStack, consumer, light, overlay, 1, 1, 1, 1);

        poseStack.popPose();
    }

    protected float getTickTime(@Nullable Level level, float partialTicks, Mode mode) {
        if (level == null || !mode.isOn)
            return 0;
        return level.getLevelData().getGameTime() + partialTicks;
    }

    @Override
    public boolean shouldRender(ExcursionFunnelEmitterBlockEntity be, Vec3 cameraPos) {
        if (!be.hasLevel())
            return false;
        BlockState state = be.getBlockState();
        for (Property<?> property : REQUIRED_PROPERTIES) {
            if (!state.hasProperty(property))
                return false;
        }
        if (state.getValue(TwoByTwoFacingMultiblockBlock.QUADRANT) != 1)
            return false;
        return BlockEntityRenderer.super.shouldRender(be, cameraPos);
    }
}
