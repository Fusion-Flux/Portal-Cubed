package com.fusionflux.portalcubed.compat.rayon.absent;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
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

    @Override
    public void simpleMove(Entity entity, MovementType movementType, Vec3d movement) {
        entity.move(movementType, movement);
    }

    @Override
    public void setNoGravity(Entity entity, boolean noGravity) {
        entity.setNoGravity(noGravity);
    }

    @Override
    public float getYaw(Entity entity) {
        return entity.getYaw();
    }

    @Override
    public void rotateYaw(Entity entity, float amount) {
        entity.setYaw(entity.getYaw() + amount);
    }

    @Override
    public void setAngularVelocityYaw(Entity entity, Vec3f angle) {
    }
}
