package com.fusionflux.portalcubed.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.BlockView;

public interface CameraExt {
    FluidState portalcubed$getSubmergedFluidState();

    void updateSimple(BlockView area, Entity focusedEntity);
}
