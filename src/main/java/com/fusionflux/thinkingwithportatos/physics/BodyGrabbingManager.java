package com.fusionflux.thinkingwithportatos.physics;

import com.fusionflux.thinkingwithportatos.client.packet.ThinkingWithPortatosClientPackets;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class keeps track of each player who is grabbing an entity. It also facilitates
 * the start and stop of each grab and prevents one entity to be grabbed by two players.
 * One instance exists on the logical client and one on the logical server.
 */
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

        grabInstances.values().forEach(grabInstance -> {
            grabInstance.grabbedBody.activate();
            grabInstance.grabPoint.setPhysicsLocation(VectorHelper.vec3dToVector3f(
                    grabInstance.player.getCameraPosVec(1.0f).add(grabInstance.player.getRotationVector().multiply(2f)))
            );
        });
    }

    public void tick() {
        grabInstances.values().forEach(grabInstance -> {
            Item mainHand = grabInstance.player.getMainHandStack().getItem();
            Item offHand = grabInstance.player.getOffHandStack().getItem();

            if (isServer && !(mainHand instanceof PortalGun || offHand instanceof PortalGun)) {
                tryUngrab(grabInstance.player, 0.0f);
            }

            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                Vector3f location = grabInstance.grabbedBody.getPhysicsLocation(new Vector3f());
                grabInstance.grabbedEntity.updatePosition(location.x, location.y - grabInstance.grabbedEntity.getBoundingBox().getYLength() / 2.0, location.z);
            }
        });
    }

    public void tryGrab(PlayerEntity player, Entity entity) {
        if (isPlayerGrabbing(player) || isGrabbed(entity)) {
            return;
        }

        if (player instanceof ServerPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(player.getEntityId());
            buf.writeInt(entity.getEntityId());
            PlayerLookup.tracking(entity).forEach(p ->
                    ServerPlayNetworking.send(p, ThinkingWithPortatosClientPackets.GRAB_PACKET, buf)
            );
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
        }

        Vector3f pos = VectorHelper.vec3dToVector3f(player.getCameraPosVec(1.0f).add(player.getRotationVector().multiply(2f)));
        PhysicsRigidBody holdBody = new PhysicsRigidBody(EMPTY_SHAPE, 0);
        holdBody.setPhysicsLocation(pos);

        SixDofSpringJoint joint = new SixDofSpringJoint(grabInstance.grabbedBody, holdBody, Vector3f.ZERO, Vector3f.ZERO, Matrix3f.IDENTITY, Matrix3f.IDENTITY, false);
        joint.setLinearLowerLimit(Vector3f.ZERO);
        joint.setLinearUpperLimit(Vector3f.ZERO);
        joint.setAngularLowerLimit(Vector3f.ZERO);
        joint.setAngularUpperLimit(Vector3f.ZERO);

        space.getThread().execute(() -> {
            if (!(entity instanceof EntityPhysicsElement)) {
                space.addCollisionObject(grabInstance.grabbedBody);
            }

            space.addCollisionObject(holdBody);
            space.addJoint(joint);
        });

        grabInstance.grabJoint = joint;
        grabInstance.grabPoint = holdBody;
        grabInstances.put(player.getUuid(), grabInstance);
    }

    public void tryUngrab(PlayerEntity player, float strength) {
        GrabInstance grabInstance = grabInstances.remove(player.getUuid());

        if (grabInstance == null) {
            return;
        }

        Vec3d unit = player.getRotationVector();

        if (player instanceof ServerPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(player.getEntityId());
            buf.writeFloat(strength);
            PlayerLookup.tracking(grabInstance.grabbedEntity).forEach(p ->
                    ServerPlayNetworking.send(p, ThinkingWithPortatosClientPackets.UNGRAB_PACKET, buf)
            );

            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                Vec3d velocity = VectorHelper.vector3fToVec3d(grabInstance.grabbedBody.getLinearVelocity(new Vector3f()).multLocal(0.05f));
                grabInstance.grabbedEntity.addVelocity(velocity.x, velocity.y, velocity.z);
                grabInstance.grabbedEntity.addVelocity(unit.x * strength * 0.05f, unit.y * strength * 0.05f, unit.z * strength * 0.05f);
            }
        }

        MinecraftSpace space = MinecraftSpace.get(player.world);
        SixDofSpringJoint joint = grabInstance.grabJoint;

        space.getThread().execute(() -> {
            if (strength > 0.0f) {
                Random rand = new Random();
                grabInstance.grabbedBody.setAngularVelocity(new Vector3f(rand.nextFloat() * 4 - 2, rand.nextFloat() * 4 - 2, rand.nextFloat() * 4 - 2));
                grabInstance.grabbedBody.applyCentralImpulse(VectorHelper.vec3dToVector3f(unit).multLocal(strength).multLocal(grabInstance.grabbedBody.getMass()));
            }

            if (grabInstance.grabbedBody instanceof EntityRigidBody) {
                space.removeCollisionObject(grabInstance.grabbedBody);
            }

            space.removeCollisionObject(grabInstance.grabPoint);
            space.removeJoint(joint);
        });
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
