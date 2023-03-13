package com.fusionflux.portalcubed.compat.rayon.absent;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public enum RayonIntegrationAbsent implements RayonIntegration {
    INSTANCE;

    @Override
    public void init() {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public void setVelocity(Entity entity, Vec3d velocity) {
        entity.setVelocity(velocity);
    }

    @Override
    public Quaternion getVisualRotation(Entity entity, float tickDelta) {
        // TODO: include pitch as well
        return Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - entity.getYaw(tickDelta));
    }
}
