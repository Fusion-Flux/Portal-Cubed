package com.fusionflux.portalcubed.mixin;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClipContext.class)
public interface RaycastContextAccessor {
    @Accessor
    ClipContext.Block getBlock();

    @Accessor
    ClipContext.Fluid getFluid();

    @Accessor
    CollisionContext getCollisionContext();
}
