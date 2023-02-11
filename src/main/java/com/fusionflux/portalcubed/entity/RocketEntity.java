package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RocketEntity extends Entity {
    private static final double SPEED = 1;

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        setVelocity(Vec3d.fromPolar(getPitch(), getYaw()).multiply(SPEED));
        move(MovementType.SELF, getVelocity());
        if (world.isClient) {
            for (int i = 0; i < 2; i++) {
                world.addParticle(
                    ParticleTypes.POOF,
                    getX() + random.nextGaussian() * 0.2,
                    getY() + random.nextGaussian() * 0.2,
                    getZ() + random.nextGaussian() * 0.2,
                    getVelocity().x, getVelocity().y, getVelocity().z
                );
            }
            return;
        }
        final HitResult hit = ProjectileUtil.getCollision(this, this::canHit);
        if (hit.getType() != HitResult.Type.MISS) {
            explode(hit instanceof EntityHitResult entityHit ? (LivingEntity)entityHit.getEntity() : null);
        } else if (horizontalCollision || verticalCollision) {
            explode(null);
        }
        if (age > 0 && age % 13 == 0) {
            world.playSoundFromEntity(null, this, PortalCubedSounds.ROCKET_FLY_EVENT, SoundCategory.HOSTILE, 1, 1);
        }
        if (age > 200) {
            explode(null);
        }
    }

    protected boolean canHit(Entity entity) {
        return entity instanceof LivingEntity && !entity.isSpectator() && entity.isAlive() && entity.collides();
    }

    public void explode(@Nullable LivingEntity entity) {
        if (entity != null) {
            entity.damage(
                new ProjectileDamageSource("fireworks", this, null).setExplosive(),
                PortalCubedConfig.rocketDamage
            );
        }
        world.playSound(null, getX(), getY(), getZ(), PortalCubedSounds.ROCKET_EXPLODE_EVENT, SoundCategory.HOSTILE, 1, 1);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                getX(), getY(), getZ(),
                8,
                0.5, 0.5, 0.5,
                0
            );
        }
        kill();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason == RemovalReason.UNLOADED_TO_CHUNK) {
            reason = RemovalReason.DISCARDED;
        }
        super.remove(reason);
    }
}
