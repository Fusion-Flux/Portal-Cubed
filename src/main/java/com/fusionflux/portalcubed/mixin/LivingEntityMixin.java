package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityAccessor {
    @Shadow protected boolean jumping;

    @Override
    public boolean isJumping() {
        return jumping;
    }
}
