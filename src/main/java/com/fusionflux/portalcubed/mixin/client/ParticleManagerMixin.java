package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.SpecialHiddenBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @WrapOperation(
        method = "addBlockBreakingParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
        )
    )
    private BlockRenderType fixSpecialHiddenCrash(BlockState blockState, Operation<BlockRenderType> original) {
        if (blockState.getBlock() instanceof SpecialHiddenBlock) {
            return BlockRenderType.INVISIBLE;
        }
        return original.call(blockState);
    }
}
