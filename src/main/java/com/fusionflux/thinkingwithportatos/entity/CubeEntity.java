package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.jme3.math.Vector3f;

import dev.lazurite.rayon.core.api.PhysicsElement;

import dev.lazurite.rayon.core.api.event.ElementCollisionEvents;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class CubeEntity extends Entity implements EntityPhysicsElement {
    public static final Identifier SPAWN_PACKET = id("spawn_cube");
    protected final ElementRigidBody RIGID_BODY = new ElementRigidBody(this);
    private float storedDamage = 0.0F;
    private int storedAge=0;
    protected final EntityTrackingSoundInstance CUBESCRAPENOISE = new EntityTrackingSoundInstance(ThinkingWithPortatosSounds.CUBE_SCRAPE_EVENT, this.getSoundCategory(), .05F, 1F, this);



    public CubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        PhysicsThread.get(world).execute(() -> {
            this.getRigidBody().setCollisionShape(new BoundingBoxShape(this.getBoundingBox()));
            this.getRigidBody().setMass(1.0f);                 // 0.0f - ? kg
            this.getRigidBody().setFriction(0.8f);             // 0.0f - 1.0f
            this.getRigidBody().setRestitution(0.3f);          // 0.0f - 1.0f
            this.getRigidBody().setDragCoefficient(0.0f);     // 0.0f - ?
            this.getRigidBody().setEnvironmentLoadDistance(1); // 1 - ? (affects performance extremely)
            this.getRigidBody().setDoFluidResistance(false);
        });
        ElementCollisionEvents.BLOCK_COLLISION.register((thread, element, block, impulse) -> {
            if (element instanceof CubeEntity) {
                thread.execute(() -> {
                if(!((CubeEntity) element).world.isClient) {
                    if (impulse >= .15 && impulse <= .5) {
                        if (Math.abs(this.age - this.storedAge) > 2) {
                            ((CubeEntity) element).world.playSound(null, ((CubeEntity) element).getPos().getX(), ((CubeEntity) element).getPos().getY(), ((CubeEntity) element).getPos().getZ(), ThinkingWithPortatosSounds.CUBE_LOW_HIT_EVENT, SoundCategory.NEUTRAL, .15f, 1F);
                            //System.out.println("lowimpact");
                            this.storedAge = this.age;
                        }
                    }
                    if (impulse >= .5) {
                        if (Math.abs(this.age - this.storedAge) > 2) {
                            ((CubeEntity) element).world.playSound(null, ((CubeEntity) element).getPos().getX(), ((CubeEntity) element).getPos().getY(), ((CubeEntity) element).getPos().getZ(), ThinkingWithPortatosSounds.CUBE_HIGH_HIT_EVENT, SoundCategory.NEUTRAL, .15f, 1F);
                            //System.out.println("highimpact");
                            this.storedAge = this.age;
                        }
                    }
                }
                });
            }
        });
    }



    @Override
    public void tick() {
        super.tick();
        doDamage();
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
        return true;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return false;
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
