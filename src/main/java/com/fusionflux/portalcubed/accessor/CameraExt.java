package com.fusionflux.portalcubed.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public interface CameraExt {
	FluidState portalcubed$getSubmergedFluidState();

	void updateSimple(BlockGetter area, Entity focusedEntity);

	void backCameraUp(Vec3 from);
}
