package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CubeEntity extends Entity implements EntityPhysicsElement {
    protected final ElementRigidBody RIGID_BODY = new ElementRigidBody(this);
    private float storedDamage = 0.0F;
    private int storedAge = 0;

    public CubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.getRigidBody().setMass(1.0f);
        this.getRigidBody().setFriction(1.5f);
        this.getRigidBody().setRestitution(0.3f);
        this.getRigidBody().setDragCoefficient(0.001f);
    }

    @Override
    public void tick() {
        super.tick();
        doDamage();
    }

    public void onCollision(float impulse) {
        if (!world.isClient()) {
          //  System.out.println(impulse);
            if (impulse > .1 && impulse < 5) {
                //if (Math.abs(this.age - this.storedAge) > 2) {
                    world.playSound(null, getX(), getY(), getZ(), ThinkingWithPortatosSounds.CUBE_LOW_HIT_EVENT, SoundCategory.NEUTRAL, .25f, 1F);
                    //System.out.println("lowimpact"+impulse);
                    //this.storedAge = this.age;
                //}
            }
            if (impulse >= 5) {
                //if (Math.abs(this.age - this.storedAge) > 2) {
                    world.playSound(null, getX(), getY(), getZ(), ThinkingWithPortatosSounds.CUBE_HIGH_HIT_EVENT, SoundCategory.NEUTRAL, .25f, 1F);
                   // System.out.println("highimpact"+impulse);
                   // this.storedAge = this.age;
                }
           // }
        }
    }

    private void doDamage() {
        float velocity = getRigidBody().getLinearVelocity(new Vector3f()).length();
        float momentum = velocity * getRigidBody().getMass();

        if (velocity >= 15) {
            for (Entity entity : this.getEntityWorld().getOtherEntities(this, this.getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                entity.damage(DamageSource.GENERIC, momentum / 20.0f);

                /* Loses 90% of its speed */
                PhysicsThread.get(world).execute(() ->
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
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).abilities.creativeMode;
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
        return getSpawnPacket();
    }

    @Override
    public void step(MinecraftSpace space) {

    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.RIGID_BODY;
    }
}
