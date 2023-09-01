package com.fusionflux.portalcubed.compat.rayon.present;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
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
	public void setVelocity(Entity entity, Vec3 velocity) {
		if (entity instanceof EntityPhysicsElement physicsElement) {
			physicsElement.getRigidBody().setLinearVelocity(Convert.toBullet(velocity.scale(14.5)));
		} else {
			entity.setDeltaMovement(velocity);
		}
	}

	@Override
	public void simpleMove(Entity entity, MoverType movementType, Vec3 movement) {
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
			// https://stackoverflow.com/a/5783030/8840278
			return (float)Math.toDegrees(2 * Math.acos(q.getW()));
		}
		return entity.getYRot();
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
			entity.setYRot(entity.getYRot() + amount);
		}
	}

	@Override
	public void setAngularVelocityYaw(Entity entity, org.joml.Vector3f angle) {
		if (entity instanceof EntityPhysicsElement physicsElement) {
			physicsElement.getRigidBody().setAngularVelocity(Convert.toBullet(angle));
		}
	}

	@Override
	@ClientOnly
	public void multiplyMatrices(PoseStack matrices, Entity entity, float tickDelta) {
		if (entity instanceof EntityPhysicsElement physicsElement) {
			matrices.mulPose(Convert.toMinecraft(physicsElement.getPhysicsRotation(new com.jme3.math.Quaternion(), tickDelta)));
		} else {
			RayonIntegrationAbsent.INSTANCE.multiplyMatrices(matrices, entity, tickDelta);
		}
	}

}
