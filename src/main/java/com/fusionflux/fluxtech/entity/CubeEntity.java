package com.fusionflux.fluxtech.entity;

import com.fusionflux.fluxtech.FluxTech;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class CubeEntity extends Entity implements PhysicsElement {
    private final ElementRigidBody RIGID_BODY = new ElementRigidBody(this);
    public static final Identifier SPAWN_PACKET = new Identifier(FluxTech.MOD_ID, "cube");

    public CubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        Rayon.THREAD.get(world).execute(space -> {
            this.RIGID_BODY.setCollisionShape(new BoundingBoxShape(this.getBoundingBox()));
            this.RIGID_BODY.setMass(1.0f);              // 0.0f - ? kg
            this.RIGID_BODY.setFriction(0.5f);          // 0.0f - 1.0f
            this.RIGID_BODY.setRestitution(0.5f);       // 0.0f - 1.0f
            this.RIGID_BODY.setDragCoefficient(0.05f);  // 0.0f - ?
            this.RIGID_BODY.setBlockLoadDistance(1);    // 1 - ? (affects performance extremely)
            this.RIGID_BODY.setDoFluidResistance(true);
        });
    }

    @Override
    public void tick() {
        super.tick();
        doDamage();
    }

    private void doDamage() {

        /* Velocity */
        float v = getRigidBody().getLinearVelocity(new Vector3f()).length();

        /* Momentum */
        float p = v * getRigidBody().getMass();

        if (v >= 15) {
            for (Entity entity : this.getEntityWorld().getOtherEntities(this, this.getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                entity.damage(DamageSource.GENERIC, p / 20.0f);

                /* Loses 90% of its speed */
                Rayon.THREAD.get(asEntity().getEntityWorld()).execute(space ->
                        getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass()))
                );
            }
        }
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    //TODO
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()));
        packet.writeUuid(this.getUuid());
        packet.writeVarInt(this.getEntityId());
        packet.writeDouble(this.getX());
        packet.writeDouble(this.getY());
        packet.writeDouble(this.getZ());
        packet.writeByte(MathHelper.floor(this.pitch * 256.0F / 360.0F));
        packet.writeByte(MathHelper.floor(this.yaw * 256.0F / 360.0F));

        return ServerPlayNetworking.createS2CPacket(SPAWN_PACKET, packet);
    }

    @Override
    public void step(MinecraftSpace space) {

    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.RIGID_BODY;
    }
}
