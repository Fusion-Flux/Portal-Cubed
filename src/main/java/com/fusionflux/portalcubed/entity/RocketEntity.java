package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class RocketEntity extends Entity implements Fizzleable {
	private static final double SPEED = 1;

	private float fizzleProgress = 0f;
	private boolean fizzling = false;

	public RocketEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
	}

	@Override
	public void tick() {
		super.tick();
		if (fizzling) {
			if (level().isClientSide) {
				fizzleProgress += Minecraft.getInstance().getFrameTime();
			} else {
				fizzleProgress += 0.05f;
				if (fizzleProgress >= 1f) {
					remove(RemovalReason.KILLED);
				}
			}
		} else {
			setDeltaMovement(Vec3.directionFromRotation(getXRot(), getYRot()).scale(SPEED));
		}
		move(MoverType.SELF, getDeltaMovement());
		if (!level().isClientSide && tickCount > 0 && tickCount % 13 == 0) {
			level().playSound(null, this, PortalCubedSounds.ROCKET_FLY_EVENT, SoundSource.HOSTILE, 1, 1);
		}
		if (fizzling) return;
		if (level().isClientSide) {
			level().addParticle(
				ParticleTypes.SMOKE,
				getX() + random.nextGaussian() * 0.1,
				getY() + random.nextGaussian() * 0.1,
				getZ() + random.nextGaussian() * 0.1,
				0, 0, 0
			);
			level().addParticle(
				ParticleTypes.SMALL_FLAME,
				getX() + random.nextGaussian() * 0.1,
				getY() + random.nextGaussian() * 0.1,
				getZ() + random.nextGaussian() * 0.1,
				0, 0, 0
			);
			return;
		}
		final HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHit);
		if (hit.getType() == HitResult.Type.ENTITY) {
			explode((LivingEntity)((EntityHitResult)hit).getEntity());
		} else {
			final LivingEntity hit2 = level().getNearestEntity(
				LivingEntity.class,
				TargetingConditions.forNonCombat().selector(this::canHit),
				null, getX(), getY(), getZ(),
				getBoundingBox()
			);
			if (hit2 != null) {
				explode(hit2);
			} else if (horizontalCollision || verticalCollision) {
				explode(null);
			}
		}
		if (tickCount > 200) {
			explode(null);
		}
	}

	@Override
	protected void onInsideBlock(BlockState state) {
		if (
			state.getFluidState().is(PortalCubedFluids.TOXIC_GOO.still) ||
				state.getFluidState().is(PortalCubedFluids.TOXIC_GOO.flowing)
		) {
			level().playSound(null, getX(), getY(), getZ(), PortalCubedSounds.ROCKET_GOO_EVENT, SoundSource.HOSTILE, 1, 1);
			kill();
		}
	}

	protected boolean canHit(Entity entity) {
		return entity instanceof LivingEntity && !entity.isSpectator() && entity.isAlive() && entity.isPickable();
	}

	public void explode(@Nullable LivingEntity entity) {
		if (entity != null) {
			entity.hurt(
				damageSources().source(DamageTypes.FIREWORKS, this),
				PortalCubedConfig.rocketDamage
			);
		}
		level().playSound(null, getX(), getY(), getZ(), PortalCubedSounds.ROCKET_EXPLODE_EVENT, SoundSource.HOSTILE, 1, 1);
		if (level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(
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
	public void startFizzlingProgress() {
		fizzling = true;
		setDeltaMovement(getDeltaMovement().scale(0.2));
	}

	@Override
	public void fizzle() {
		if (fizzling) return;
		startFizzlingProgress();
		final FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(getId());
		final Packet<?> packet = ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.FIZZLE_PACKET, buf);
		PlayerLookup.tracking(this).forEach(player -> player.connection.send(packet));
	}

	@Override
	public float getFizzleProgress() {
		return fizzleProgress;
	}

	@Override
	public boolean fizzling() {
		return fizzling;
	}

	@Override
	public boolean fizzlesInGoo() {
		return false;
	}

	@Override
	public FizzleType getFizzleType() {
		return FizzleType.OBJECT;
	}

	@Override
	public void remove(@NotNull RemovalReason reason) {
		if (reason == RemovalReason.UNLOADED_TO_CHUNK) {
			reason = RemovalReason.DISCARDED;
		}
		super.remove(reason);
	}
}
