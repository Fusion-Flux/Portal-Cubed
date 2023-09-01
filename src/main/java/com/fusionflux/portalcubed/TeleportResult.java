package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.util.IPQuaternion;
import net.minecraft.world.phys.Vec3;

public record TeleportResult(
	Vec3 dest,
	float yaw,
	float pitch,
	Vec3 velocity,
	IPQuaternion immediateFinalRot
) {
}
