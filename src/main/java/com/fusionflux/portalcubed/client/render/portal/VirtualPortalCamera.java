package com.fusionflux.portalcubed.client.render.portal;

import org.joml.Vector3f;

import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.mixin.client.CameraAccessor;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public final class VirtualPortalCamera {
	private static Vec3 beforeUsagePos;
	private static Vector3f beforeUsageRotation;

	private VirtualPortalCamera() { }

	private static Vec3 transformedPos(Portal portal) {
		// TODO
		return beforeUsagePos;
		// return beforeUsagePos
		// 	.subtract(portal.getOriginPos())
		// 	.add(
		// 		portal.getLinkedPortalUUID().map(uuid ->
		// 			((Portal) ((ClientLevel) ((Portal) portal).level()).getEntities().get(uuid)).getOriginPos()).orElse(Vec3.ZERO));
	}

	private static Vector3f transformedRotation(Portal portal, float tickDelta) {
		// TODO: Confirm this works
		var relativeRot = portal.getRotation().get(tickDelta).transformInverse(new Vector3f(beforeUsageRotation));
		relativeRot = Axis.YP.rotationDegrees(180).transform(relativeRot);
		var outRotation = portal.getOtherRotation();
		return outRotation.isPresent() ? outRotation.get().transform(relativeRot) : beforeUsageRotation;
	}

	public static void setup(Portal portal, float tickDelta) {
		final var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();

		beforeUsagePos = mainCamera.getPosition();
		beforeUsageRotation = new Vector3f(mainCamera.getYRot(), mainCamera.getXRot(), 0);

		((CameraAccessor) mainCamera).portalcubed$setPosition(transformedPos(portal));
		final var transformedRotation = transformedRotation(portal, tickDelta);
		((CameraAccessor) mainCamera).portalcubed$setRotation(transformedRotation.x, transformedRotation.y);
	}

	public static void reset() {
		final var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();

		((CameraAccessor) mainCamera).portalcubed$setPosition(beforeUsagePos);
		((CameraAccessor) mainCamera).portalcubed$setRotation(beforeUsageRotation.x, beforeUsageRotation.y);
	}
}
