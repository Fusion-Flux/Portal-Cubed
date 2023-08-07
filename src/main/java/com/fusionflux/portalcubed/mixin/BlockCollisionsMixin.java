package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.BlockCollisionsExt;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin<T> implements BlockCollisionsExt<T> {
    @Unique
    private static final VoxelShape empty = Shapes.empty();

    @Unique
    private VoxelShape pc$portalCutout;
    @Unique
    private VoxelShape pc$crossCollision;

    @Override
    @SuppressWarnings("unchecked")
    public BlockCollisions<T> setExtraShapes(VoxelShape cutout, VoxelShape crossCollision) {
        pc$portalCutout = cutout;
        pc$crossCollision = crossCollision;
        return (BlockCollisions<T>)(Object)this;
    }

    @ModifyExpressionValue(
        method = "computeNext",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
        )
    )
    private VoxelShape handlePortalCollisions(VoxelShape shape,
                                              @Local(ordinal = 0) int x,
                                              @Local(ordinal = 1) int y,
                                              @Local(ordinal = 2) int z) {

        if (pc$portalCutout != null && pc$portalCutout != empty) {
            shape = Shapes.joinUnoptimized(shape, pc$portalCutout.move(-x, -y, -z), BooleanOp.ONLY_FIRST);
        }
        if (pc$crossCollision != null && pc$portalCutout != empty) {
            shape = Shapes.joinUnoptimized(shape, pc$crossCollision.move(-x, -y, -z), BooleanOp.OR);
        }

        return shape;
    }
}
