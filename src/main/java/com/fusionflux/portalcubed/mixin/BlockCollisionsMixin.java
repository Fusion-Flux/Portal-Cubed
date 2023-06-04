package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.BlockCollisionsExt;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin implements BlockCollisionsExt {
    @Unique
    private VoxelShape pc$portalCutout = Shapes.empty();

    @Override
    public BlockCollisions setPortalCutout(VoxelShape cutout) {
        pc$portalCutout = cutout;
        return (BlockCollisions)(Object)this;
    }

    @WrapOperation(
        method = "computeNext",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
        )
    )
    private VoxelShape applyCollisionShape(
        BlockState instance, BlockGetter level, BlockPos pos, CollisionContext context,
        Operation<VoxelShape> original,
        @Local(ordinal = 0) int x, @Local(ordinal = 1) int y, @Local(ordinal = 2) int z
    ) {
        return Shapes.joinUnoptimized(
            original.call(instance, level, pos, context),
            pc$portalCutout.move(-x, -y, -z),
            BooleanOp.ONLY_FIRST
        );
    }
}
