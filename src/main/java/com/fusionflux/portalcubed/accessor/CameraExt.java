package com.fusionflux.portalcubed.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;

public interface CameraExt {
    FluidState portalcubed$getSubmergedFluidState();

    void updateSimple(BlockGetter area, Entity focusedEntity);
}
