package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.BlockCollisionsExt;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {
    @WrapOperation(
        method = "noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/CollisionGetter;getBlockCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;"
        )
    )
    default Iterable<VoxelShape> supportCutout(CollisionGetter instance, Entity entity, AABB collisionBox, Operation<Iterable<VoxelShape>> original) {
        return BlockCollisionsExt.wrapBlockCollisions(original.call(instance, entity, collisionBox), entity);
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
