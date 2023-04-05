package com.fusionflux.portalcubed.compat.rayon.present;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class RayonIntegrationPresent implements RayonIntegration {
    private static final Vector3f UP = new Vector3f(0, 1, 0);

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
    public void simpleMove(Entity entity, MovementType movementType, Vec3d movement) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            physicsElement.getRigidBody().setPhysicsLocation(
                physicsElement.getRigidBody()
                    .getPhysicsLocation(new Vector3f())
                    .add(Convert.toBullet(movement))
            );
        } else {
            entity.move(movementType, movement);
        }
    }

    @Override
    public void setNoGravity(Entity entity, boolean noGravity) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            physicsElement.getRigidBody().setKinematic(noGravity);
        }
        entity.setNoGravity(noGravity);
    }

    @Override
    public float getYaw(Entity entity) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            final com.jme3.math.Quaternion q = physicsElement.getRigidBody().getPhysicsRotation(null);
            https://stackoverflow.com/a/5783030/8840278
            return (float)Math.toDegrees(2 * Math.acos(q.getW()));
        }
        return entity.getYaw();
    }

    @Override
    public void rotateYaw(Entity entity, float amount) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            physicsElement.getRigidBody().setPhysicsRotation(
                physicsElement.getRigidBody()
                    .getPhysicsRotation(null)
                    .mult(new com.jme3.math.Quaternion().fromAngleNormalAxis((float)Math.toRadians(amount), UP))
            );
        } else {
            entity.setYaw(entity.getYaw() + amount);
        }
    }

    @Override
    public void setAngularVelocityYaw(Entity entity, Vec3f angle) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            physicsElement.getRigidBody().setAngularVelocity(Convert.toBullet(angle));
        }
    }

    @Override
    @ClientOnly
    public void multiplyMatrices(MatrixStack matrices, Entity entity, float tickDelta) {
        if (entity instanceof EntityPhysicsElement physicsElement) {
            matrices.multiply(Convert.toMinecraft(physicsElement.getPhysicsRotation(new com.jme3.math.Quaternion(), tickDelta)));
        } else {
            RayonIntegrationAbsent.INSTANCE.multiplyMatrices(matrices, entity, tickDelta);
        }
    }
}
