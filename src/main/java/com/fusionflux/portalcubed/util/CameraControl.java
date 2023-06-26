package com.fusionflux.portalcubed.util;

import net.minecraft.world.phys.Vec3;

public record CameraControl(Vec3 pos, float yaw, float pitch) {
    public CameraControl update(Vec3 pos, float yaw, float pitch) {
        return new CameraControl(pos, yaw, pitch);
    }
}
