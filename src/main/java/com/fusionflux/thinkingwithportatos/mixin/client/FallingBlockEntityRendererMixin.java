package com.fusionflux.thinkingwithportatos.mixin.client;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockEntityRenderer.class)
public class FallingBlockEntityRendererMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            )
    )
    public BlockState getBlockState(World world, BlockPos pos, FallingBlockEntity block) {
        if (ThinkingWithPortatos.getBodyGrabbingManager(true).isGrabbed(block) ||
                block.getVelocity().x > 0.0f || block.getVelocity().z > 0.0f) {
            return null;
        }

        return world.getBlockState(pos);
    }
}
