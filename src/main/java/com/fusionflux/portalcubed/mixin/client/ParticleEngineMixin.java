package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.SpecialHiddenBlock;
import com.fusionflux.portalcubed.client.particle.DecalParticle;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @WrapOperation(
        method = "crack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
        )
    )
    private RenderShape fixSpecialHiddenCrash(BlockState blockState, Operation<RenderShape> original) {
        if (blockState.getBlock() instanceof SpecialHiddenBlock) {
            return RenderShape.INVISIBLE;
        }
        return original.call(blockState);
    }

    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
            remap = false
        )
    )
    private static ImmutableList<?> addCustomRenderType(
        Object e1, Object e2, Object e3, Object e4, Object e5, Operation<ImmutableList<?>> original
    ) {
        return Stream.concat(
            original.call(e1, e2, e3, e4, e5).stream(),
            Stream.of(DecalParticle.PARTICLE_SHEET_MULTIPLY)
        ).collect(ImmutableList.toImmutableList());
    }
}
