package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;

public interface LivingEntityAccessor {
    boolean isJumping();

    void collidedWithVelocityHelper(VelocityHelperBlockEntity block);
}
