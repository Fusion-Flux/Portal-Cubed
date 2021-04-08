package com.fusionflux.thinkingwithportatos.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.EmptyShape;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;

import java.util.HashMap;

public class BodyGrabbingManager {
    public static EmptyShape EMPTY_SHAPE = null;
    public final boolean isServer;

    public HashMap<Entity, GrabInstance> grabInstances = new HashMap<>();

    public BodyGrabbingManager(boolean isServer) {
        this.isServer = isServer;
    }

    public void init() {
        PhysicsSpaceEvents.STEP.register(space -> {
            if (EMPTY_SHAPE == null) {
                EMPTY_SHAPE = new EmptyShape(false);
            }

            if (space.isServer() == isServer) {
                for (GrabInstance grabInstance : grabInstances.values()) {
                    Entity grabber = grabInstance.grabber;
                    Vector3f pos = VectorHelper.vec3dToVector3f(grabber.getCameraPosVec(1.0f).add(grabber.getRotationVector().multiply(2f)));
                    grabInstance.grabPoint.setPhysicsLocation(pos);
                }
            }
        });
    }

    public boolean tryGrab(Entity grabber, EntityPhysicsElement physEntity) {
        if (grabInstances.containsKey(grabber)) {
            return false;
        }

        ElementRigidBody body = physEntity.getRigidBody();
        MinecraftSpace space = body.getSpace();

        GrabInstance grabInstance = new GrabInstance();
        grabInstance.grabber = grabber;
        grabInstance.grabbedBody = physEntity;

        Vector3f pos = VectorHelper.vec3dToVector3f(grabber.getCameraPosVec(1.0f).add(grabber.getRotationVector().multiply(2f)));
        PhysicsRigidBody holdBody = new PhysicsRigidBody(EMPTY_SHAPE, 0);
        holdBody.setPhysicsLocation(pos);
        space.addCollisionObject(holdBody);
        SixDofSpringJoint joint = new SixDofSpringJoint(body, holdBody, Vector3f.ZERO, Vector3f.ZERO, Matrix3f.IDENTITY, Matrix3f.IDENTITY, false);
        joint.setLinearLowerLimit(Vector3f.ZERO);
        joint.setLinearUpperLimit(Vector3f.ZERO);
        joint.setAngularLowerLimit(Vector3f.ZERO);
        joint.setAngularUpperLimit(Vector3f.ZERO);
        space.addJoint(joint);

        grabInstance.grabJoint = joint;
        grabInstance.grabPoint = holdBody;
        grabInstances.put(grabber, grabInstance);
        return true;
    }

    public boolean tryStopGrabbing(Entity user) {
        GrabInstance grabInstance = grabInstances.remove(user);
        if (grabInstance == null) {
            return false;
        }

        SixDofSpringJoint joint = grabInstance.grabJoint;
        PhysicsSpace space = joint.getPhysicsSpace();

        space.removeCollisionObject(grabInstance.grabPoint);
        space.removeJoint(joint);
        return true;
    }

    public static class GrabInstance {
        public Entity grabber;
        public EntityPhysicsElement grabbedBody;

        public SixDofSpringJoint grabJoint;
        public PhysicsRigidBody grabPoint;
    }
}
