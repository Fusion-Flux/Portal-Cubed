package com.fusionflux.portalcubed.mixin;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapeContext.class)
public interface ShapeContextMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void ofNullable(Entity entity, CallbackInfoReturnable<ShapeContext> cir) {
        if (entity == null) {
            cir.setReturnValue(ShapeContext.absent());
        }
    }
}
