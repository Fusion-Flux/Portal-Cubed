package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.BlockCollisionsExt;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {
    @ModifyReturnValue(
        method = "getBlockCollisions",
        at = @At("RETURN")
    )
    default Iterable<VoxelShape> supportCutout(Iterable<VoxelShape> original, @Local Entity entity) {
        if (entity == null) {
            return original;
        }
        final VoxelShape cutout = CalledValues.getPortalCutout(entity);
        return () -> ((BlockCollisionsExt)original.iterator()).setPortalCutout(cutout);
    }

    @ModifyVariable(
        method = "collidesWithSuffocatingBlock",
        at = @At("STORE"),
        ordinal = 0
    )
    @SuppressWarnings("InvalidInjectorMethodSignature")
    default BlockCollisions supportCutout(BlockCollisions value, @Local Entity entity) {
        return ((BlockCollisionsExt)value).setPortalCutout(CalledValues.getPortalCutout(entity));
    }
}
