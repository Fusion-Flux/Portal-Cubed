package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyPelletEntity extends Entity {
    private static final TrackedData<Integer> STARTING_LIFE = DataTracker.registerData(EnergyPelletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LIFE = DataTracker.registerData(EnergyPelletEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public EnergyPelletEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(STARTING_LIFE, 220);
        dataTracker.startTracking(LIFE, 220);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        setLife(nbt.getInt("Life"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Life", getLife());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public int getStartingLife() {
        return dataTracker.get(STARTING_LIFE);
    }

    public void setStartingLife(int ticks) {
        dataTracker.set(STARTING_LIFE, ticks);
    }

    public int getLife() {
        return dataTracker.get(LIFE);
    }

    public void setLife(int ticks) {
        dataTracker.set(LIFE, ticks);
    }

    public void resetLife(int startingLife) {
        setStartingLife(startingLife);
        resetLife();
    }

    public void resetLife() {
        setLife(getStartingLife());
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient) return;
        Vec3d vel = getVelocity();
        move(MovementType.SELF, vel);
        velocityDirty = true;
        int life = getLife();
        if (life > 0) {
            setLife(--life);
        } else if (life == 0) {
            kill(null);
        } // life < 0 means green pellet
        if (age == 1) {
            world.playSound(null, getPos().x, getPos().y, getPos().z, PortalCubedSounds.PELLET_SPAWN_EVENT, SoundCategory.HOSTILE, 1f, 1f);
        }
        boolean bounced = false;
        if (verticalCollision) {
            vel = vel.withAxis(Direction.Axis.Y, -vel.y);
            bounced = true;
        }
        if (horizontalCollision) {
            if (getVelocity().x == 0) {
                vel = vel.withAxis(Direction.Axis.X, -vel.x);
                bounced = true;
            }
            if (getVelocity().z == 0) {
                vel = vel.withAxis(Direction.Axis.Z, -vel.z);
                bounced = true;
            }
        }
        if (bounced) {
            setVelocity(vel);
            world.playSoundFromEntity(null, this, PortalCubedSounds.PELLET_BOUNCE_EVENT, SoundCategory.HOSTILE, 0.4f, 1f);
        }
        if ((age - 1) % 34 == 0) {
            world.playSoundFromEntity(null, this, PortalCubedSounds.PELLET_TRAVEL_EVENT, SoundCategory.HOSTILE, 0.4f, 1f);
        }
        final HitResult hit = ProjectileUtil.getCollision(this, this::canHit);
        if (hit.getType() == HitResult.Type.ENTITY) {
            kill((LivingEntity)((EntityHitResult)hit).getEntity());
        } else {
            final LivingEntity hit2 = world.getClosestEntity(
                LivingEntity.class,
                TargetPredicate.createNonAttackable().setPredicate(this::canHit),
                null, getX(), getY(), getZ(),
                getBoundingBox()
            );
            if (hit2 != null) {
                kill(hit2);
            }
        }
    }

    protected boolean canHit(Entity entity) {
        return entity instanceof LivingEntity && !entity.isSpectator() && entity.isAlive() && entity.collides();
    }

    private void kill(@Nullable LivingEntity entity) {
        world.playSound(null, getPos().x, getPos().y, getPos().z, PortalCubedSounds.PELLET_EXPLODE_EVENT, SoundCategory.HOSTILE, 0.8f, 1f);
        if (entity != null) {
            entity.damage(PortalCubedDamageSources.VAPORIZATION, PortalCubedConfig.pelletDamage);
        }
        kill();
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }
}
