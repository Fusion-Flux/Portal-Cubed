package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CubeEntity extends Entity implements PhysicsElement {
    protected final ElementRigidBody RIGID_BODY = new ElementRigidBody(this);
    public static final Identifier SPAWN_PACKET = new Identifier(ThinkingWithPortatos.MOD_ID, "cube");
    private float storedDamage = 0.0F;

    public CubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        Rayon.SPACE.get(world).getThread().execute(() -> {
            this.RIGID_BODY.setCollisionShape(new BoundingBoxShape(this.getBoundingBox()));
            this.RIGID_BODY.setMass(1.0f);                 // 0.0f - ? kg
            this.RIGID_BODY.setFriction(0.8f);             // 0.0f - 1.0f
            this.RIGID_BODY.setRestitution(0.5f);          // 0.0f - 1.0f
            this.RIGID_BODY.setDragCoefficient(0.0f);     // 0.0f - ?
            this.RIGID_BODY.setEnvironmentLoadDistance(1); // 1 - ? (affects performance extremely)
            this.RIGID_BODY.setDoFluidResistance(false);
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
                Rayon.SPACE.get(world).getThread().execute(() ->
                        getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass()))
                );
            }
        }
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.removed) {
            this.storedDamage += amount;
            this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
            if (bl || this.storedDamage >= 20.0F) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    // TODO
                    this.dropItem(ThinkingWithPortatosItems.CUBE);
                }

                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
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
