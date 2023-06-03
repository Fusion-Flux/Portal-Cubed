package com.fusionflux.portalcubed.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CollisionContext.class)
public interface CollisionContextMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void ofNullable(Entity entity, CallbackInfoReturnable<CollisionContext> cir) {
        if (entity == null) {
            cir.setReturnValue(CollisionContext.empty());
        }
    }
}
