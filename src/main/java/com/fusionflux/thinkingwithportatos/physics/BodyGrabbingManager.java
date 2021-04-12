package com.fusionflux.thinkingwithportatos.physics;

import com.fusionflux.thinkingwithportatos.client.packet.ThinkingWithPortatosClientPackets;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.EmptyShape;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BodyGrabbingManager {
    public static EmptyShape EMPTY_SHAPE = null;
    public final boolean isServer;

    public HashMap<PlayerEntity, GrabInstance> grabInstances = new HashMap<>(); // TODO intToObjectMap?

    public BodyGrabbingManager(boolean isServer) {
        this.isServer = isServer;

        PhysicsSpaceEvents.STEP.register(space -> {
            if (EMPTY_SHAPE == null) {
                EMPTY_SHAPE = new EmptyShape(false);
            }

            for (GrabInstance grabInstance : grabInstances.values()) {
                PlayerEntity grabber = grabInstance.grabber;
                Vector3f pos = VectorHelper.vec3dToVector3f(grabber.getCameraPosVec(1.0f).add(grabber.getRotationVector().multiply(2f)));
                grabInstance.grabPoint.setPhysicsLocation(pos);
            }
        });
    }

    public void tick() {
        for (GrabInstance grabInstance : grabInstances.values()) {
            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                Vector3f location = grabInstance.grabbedBody.getPhysicsLocation(new Vector3f());
                Entity entity = ((EntityRigidBody) grabInstance.grabbedBody).getEntity();
                entity.updatePosition(location.x, location.y - entity.getBoundingBox().getYLength() / 2.0, location.z);
            }
        }
    }

    public boolean tryGrab(PlayerEntity grabber, Entity entity) {
        if (grabInstances.containsKey(grabber)) {
            return false;
        }

        if (isServer) {
            sendGrabPacket((ServerPlayerEntity) grabber, entity);
        }

        MinecraftSpace space = MinecraftSpace.get(grabber.getEntityWorld());
        GrabInstance grabInstance = new GrabInstance();
        grabInstance.grabber = grabber;
        ((Grabbable) entity).setGrabbed(true);

        if (entity instanceof EntityPhysicsElement) {
            grabInstance.grabbedBody = ((EntityPhysicsElement) entity).getRigidBody();
        } else {
            grabInstance.grabbedBody = new EntityRigidBody(entity);
            space.addCollisionObject(grabInstance.grabbedBody);
        }

        Vector3f pos = VectorHelper.vec3dToVector3f(grabber.getCameraPosVec(1.0f).add(grabber.getRotationVector().multiply(2f)));
        PhysicsRigidBody holdBody = new PhysicsRigidBody(EMPTY_SHAPE, 0);
        holdBody.setPhysicsLocation(pos);
        space.addCollisionObject(holdBody);
        SixDofSpringJoint joint = new SixDofSpringJoint(grabInstance.grabbedBody, holdBody, Vector3f.ZERO, Vector3f.ZERO, Matrix3f.IDENTITY, Matrix3f.IDENTITY, false);
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

    public boolean tryStopGrabbing(PlayerEntity user) {
        GrabInstance grabInstance = grabInstances.remove(user);
        if (grabInstance == null) {
            return false;
        }

        if (isServer) {
            sendUngrabPacket((ServerPlayerEntity) user);
        }

        if (grabInstance.grabbedBody instanceof EntityRigidBody) {
            ((Grabbable) ((EntityRigidBody) grabInstance.grabbedBody).getEntity()).setGrabbed(false);
        }

        SixDofSpringJoint joint = grabInstance.grabJoint;
        PhysicsSpace space = joint.getPhysicsSpace();

        space.removeCollisionObject(grabInstance.grabPoint);
        space.removeJoint(joint);
        return true;
    }

    public void sendGrabPacket(ServerPlayerEntity grabber, Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(grabber.getEntityId());
        buf.writeInt(entity.getEntityId());
        ServerPlayNetworking.send(grabber, ThinkingWithPortatosClientPackets.GRAB_PACKET, buf);
    }

    public void sendUngrabPacket(ServerPlayerEntity grabber) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(grabber.getEntityId());
        ServerPlayNetworking.send(grabber, ThinkingWithPortatosClientPackets.UNGRAB_PACKET, buf);
    }

    public @Nullable PhysicsRigidBody get(Entity entity) {
        for (GrabInstance grabInstance : grabInstances.values()) {
            PhysicsRigidBody body = grabInstance.grabbedBody;

            if (body instanceof EntityRigidBody && entity.equals(((EntityRigidBody) body).getEntity())) {
                return body;
            }
        }

        return null;
    }

    public static class GrabInstance {
        public PlayerEntity grabber;
        public PhysicsRigidBody grabbedBody;

        public SixDofSpringJoint grabJoint;
        public PhysicsRigidBody grabPoint;
    }
}
