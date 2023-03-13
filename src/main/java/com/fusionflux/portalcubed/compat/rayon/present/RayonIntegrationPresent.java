package com.fusionflux.portalcubed.compat.rayon.present;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RayonIntegrationPresent implements RayonIntegration {
    @Override
    public void init() {
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public void setVelocity(Entity entity, Vec3d velocity) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            physicsElement.getRigidBody().setLinearVelocity(Convert.toBullet(velocity.multiply(14.5)));
        } else {
            entity.setVelocity(velocity);
        }
    }

    @Override
    public Quaternion getVisualRotation(Entity entity, float tickDelta) {
        return entity instanceof EntityPhysicsElement physicsElement
            ? Convert.toMinecraft(physicsElement.getPhysicsRotation(new com.jme3.math.Quaternion(), tickDelta))
            : RayonIntegrationAbsent.INSTANCE.getVisualRotation(entity, tickDelta);
    }
}
