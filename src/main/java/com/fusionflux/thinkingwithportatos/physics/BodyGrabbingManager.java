package com.fusionflux.thinkingwithportatos.physics;

import com.fusionflux.thinkingwithportatos.client.packet.ThinkingWithPortatosClientPackets;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
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
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BodyGrabbingManager {
    public static EmptyShape EMPTY_SHAPE = null;
    public final boolean isServer;

    public Map<UUID, GrabInstance> grabInstances = new ConcurrentHashMap<>();

    public BodyGrabbingManager(boolean isServer) {
        this.isServer = isServer;
        PhysicsSpaceEvents.STEP.register(this::step);
    }

    public void step(MinecraftSpace space) {
        if (EMPTY_SHAPE == null) {
            EMPTY_SHAPE = new EmptyShape(false);
        }

        grabInstances.values().forEach(grabInstance ->
            grabInstance.grabPoint.setPhysicsLocation(VectorHelper.vec3dToVector3f(
                    grabInstance.player.getCameraPosVec(1.0f).add(grabInstance.player.getRotationVector().multiply(2f)))));
    }

    public void tick() {
        grabInstances.values().forEach(grabInstance -> {
            if (isServer && !(grabInstance.player.getMainHandStack().getItem() instanceof PortalGun)) {
                tryUngrab(grabInstance.player);
            }

            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                Vector3f location = grabInstance.grabbedBody.getPhysicsLocation(new Vector3f());
                grabInstance.grabbedEntity.updatePosition(location.x, location.y - grabInstance.grabbedEntity.getBoundingBox().getYLength() / 2.0, location.z);
            }
        });
    }

    public boolean tryGrab(PlayerEntity player, Entity entity) {
        if (grabInstances.containsKey(player.getUuid())) {
            return false;
        }

        if (player instanceof ServerPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(player.getEntityId());
            buf.writeInt(entity.getEntityId());
            PlayerLookup.tracking(entity).forEach(p ->
                    ServerPlayNetworking.send(p, ThinkingWithPortatosClientPackets.GRAB_PACKET, buf));
        }

        MinecraftSpace space = MinecraftSpace.get(player.getEntityWorld());
        GrabInstance grabInstance = new GrabInstance();
        grabInstance.player = player;
        grabInstance.grabbedEntity = entity;

        if (entity instanceof EntityPhysicsElement) {
            grabInstance.grabbedBody = ((EntityPhysicsElement) entity).getRigidBody();
            grabInstance.grabbedBody.activate();
        } else {
            grabInstance.grabbedBody = new EntityRigidBody(entity);
            space.addCollisionObject(grabInstance.grabbedBody);
        }

        Vector3f pos = VectorHelper.vec3dToVector3f(player.getCameraPosVec(1.0f).add(player.getRotationVector().multiply(2f)));
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
        grabInstances.put(player.getUuid(), grabInstance);

        return true;
    }

    public boolean tryUngrab(PlayerEntity player) {
        GrabInstance grabInstance = grabInstances.remove(player.getUuid());

        if (grabInstance == null) {
            return false;
        }

        if (player instanceof ServerPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(player.getEntityId());
            PlayerLookup.tracking(grabInstance.grabbedEntity).forEach(p ->
                    ServerPlayNetworking.send(p, ThinkingWithPortatosClientPackets.UNGRAB_PACKET, buf));

            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                grabInstance.grabbedEntity.setVelocity(VectorHelper.vector3fToVec3d(grabInstance.grabbedBody.getLinearVelocity(new Vector3f()).multLocal(0.05f)));

                if (grabInstance.grabbedEntity instanceof FallingBlockEntity) {
                    ((FallingBlockEntity) grabInstance.grabbedEntity).setFallingBlockPos(grabInstance.grabbedEntity.getBlockPos());
                }
            }
        }

        SixDofSpringJoint joint = grabInstance.grabJoint;
        PhysicsSpace space = joint.getPhysicsSpace();

        if (grabInstance.grabbedBody instanceof EntityRigidBody) {
            space.removeCollisionObject(grabInstance.grabbedBody);
        }

        space.removeCollisionObject(grabInstance.grabPoint);
        space.removeJoint(joint);

        return true;
    }

    public boolean isGrabbed(Entity entity) {
        for (GrabInstance grabInstance : grabInstances.values()) {
            if (grabInstance.grabbedEntity.equals(entity)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPlayerGrabbing(PlayerEntity player) {
        return grabInstances.containsKey(player.getUuid());
    }

    public static class GrabInstance {
        public PlayerEntity player;

        public Entity grabbedEntity;
        public PhysicsRigidBody grabbedBody;

        public SixDofSpringJoint grabJoint;
        public PhysicsRigidBody grabPoint;
    }
}
