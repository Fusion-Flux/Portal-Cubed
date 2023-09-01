package com.fusionflux.portalcubed.util;

import net.minecraft.world.phys.Vec3;

public class CameraControl {
	private Vec3 pos;
	private float yaw, pitch;

	public CameraControl(Vec3 pos, float yaw, float pitch) {
		this.pos = pos;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Vec3 getPos() {
		return pos;
	}

	public void setPos(Vec3 pos) {
		this.pos = pos;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
