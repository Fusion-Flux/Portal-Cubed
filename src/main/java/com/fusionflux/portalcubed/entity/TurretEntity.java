package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.particle.DecalParticleOption;
import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TurretEntity extends CorePhysicsEntity {
    private static final EntityDataAccessor<Float> PITCH_SPEED = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.FLOAT);

    public static final float MODEL_SCALE = Mth.lerp(0.875f, 1 / 1.62f, 1f);
    private static final AABB BASE_BOX = createFootBox(0.5f * MODEL_SCALE, 1.5f * MODEL_SCALE, MODEL_SCALE);
    private static final float FALL_SPEED = 0.3f;

    public TurretEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(PITCH_SPEED, 0f);
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        AABB fallenBox = GeneralUtil.rotate(BASE_BOX, getXRot(), Direction.Axis.X);
        if (fallenBox != BASE_BOX) {
            fallenBox = fallenBox.move(0, MODEL_SCALE / 2, 0);
        }
        final float yaw = Mth.wrapDegrees(yHeadRot);
        AABB result = GeneralUtil.rotate(fallenBox, yaw, Direction.Axis.Y);
        if (yaw >= 45 || yaw < -135) {
            result = new AABB(-result.minX, result.minY, -result.minZ, -result.maxX, result.maxY, -result.maxZ);
        }
        return result.move(position());
    }

    @Override
    public void tick() {
        float pitchSpeed = getPitchSpeed();
        float pitch = getXRot();
        if (!RayonIntegration.INSTANCE.isPresent()) {
            if (pitchSpeed > 90) {
                pitchSpeed = 90;
            } else if (pitchSpeed < -90) {
                pitchSpeed = -90;
            }
        }
        super.tick();
        if (!RayonIntegration.INSTANCE.isPresent()) {
            setXRot(Mth.wrapDegrees(pitch + pitchSpeed));
            if (getXRot() > 90) {
                setXRot(90);
                pitchSpeed = 0;
            } else if (getXRot() < -90) {
                setXRot(-90);
                pitchSpeed = 0;
            }
            pitchSpeed += FALL_SPEED * Mth.sign(pitchSpeed) * Math.sqrt(Math.abs(pitch));
            setPitchSpeed(pitchSpeed);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("PitchSpeed", getPitchSpeed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setPitchSpeed(nbt.getFloat("PitchSpeed"));
    }

    public float getPitchSpeed() {
        return getEntityData().get(PITCH_SPEED);
    }

    public void setPitchSpeed(float pitchSpeed) {
        getEntityData().set(PITCH_SPEED, pitchSpeed);
    }

    public static void makeBulletHole(ServerLevel level, BlockHitResult hit, SoundSource soundCategory) {
        final BlockState block = level.getBlockState(hit.getBlockPos());
        final Vec3 pos = hit.getLocation().add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(0.01));
        final SoundEvent soundEffect;
        final ResourceLocation particleTexture;
        boolean multiplyTexture = true;
        if (block.is(PortalCubedBlocks.BULLET_HOLE_CONCRETE)) {
            soundEffect = PortalCubedSounds.BULLET_CONCRETE_EVENT;
            particleTexture = DecalParticleOption.BULLET_HOLE_CONCRETE;
            level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, block),
                pos.x, pos.y, pos.z, 3, 0.1, 0.1, 0.1, 1
            );
        } else if (block.is(PortalCubedBlocks.BULLET_HOLE_GLASS)) {
            soundEffect = PortalCubedSounds.BULLET_GLASS_EVENT;
            particleTexture = DecalParticleOption.BULLET_HOLE_GLASS;
            multiplyTexture = false;
        } else if (block.is(PortalCubedBlocks.BULLET_HOLE_METAL)) {
            soundEffect = PortalCubedSounds.BULLET_METAL_EVENT;
            particleTexture = DecalParticleOption.BULLET_HOLE_METAL;
        } else {
            soundEffect = null;
            particleTexture = null;
        }
        if (soundEffect != null) {
            level.playSound(null, pos.x, pos.y, pos.z, soundEffect, soundCategory, 0.3f, 1f);
        }
        if (particleTexture != null) {
            level.sendParticles(
                new DecalParticleOption(particleTexture, hit.getDirection(), multiplyTexture),
                pos.x, pos.y, pos.z, 0, 0, 0, 0, 0
            );
        }
    }
}
