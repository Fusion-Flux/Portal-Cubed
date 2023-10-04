package com.fusionflux.portalcubed.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

@Mixin(Camera.class)
public interface CameraAccessor {
	@Invoker("setPosition")
	void portalcubed$setPosition(Vec3 pos);

	@Invoker("setRotation")
	void portalcubed$setRotation(float yRot, float xRot);
}
