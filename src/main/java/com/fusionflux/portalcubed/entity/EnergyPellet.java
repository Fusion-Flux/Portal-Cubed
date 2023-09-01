package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.advancements.triggers.PortalCubedTriggers;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.listeners.WentThroughPortalListener;
import com.fusionflux.portalcubed.particle.DecalParticleOption;
import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

public class EnergyPellet extends Entity implements ItemSupplier, WentThroughPortalListener {
	private static final EntityDataAccessor<Integer> STARTING_LIFE = SynchedEntityData.defineId(EnergyPellet.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(EnergyPellet.class, EntityDataSerializers.INT);

	private int bounces;
	private UUID thrower = Util.NIL_UUID;

	public EnergyPellet(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(STARTING_LIFE, 220);
		entityData.define(LIFE, 220);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		setStartingLife(nbt.getInt("StartingLife"));
		setLife(nbt.getInt("Life"));
		bounces = nbt.getInt("Bounces");
		thrower = nbt.getUUID("Thrower");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putInt("StartingLife", getStartingLife());
		nbt.putInt("Life", getLife());
		nbt.putInt("Bounces", bounces);
		nbt.putUUID("Thrower", thrower);
	}

	public int getStartingLife() {
		return entityData.get(STARTING_LIFE);
	}

	public void setStartingLife(int ticks) {
		entityData.set(STARTING_LIFE, ticks);
	}

	public int getLife() {
		return entityData.get(LIFE);
	}

	public void setLife(int ticks) {
		entityData.set(LIFE, ticks);
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
		if (level().isClientSide) return;
		Vec3 vel = getDeltaMovement();
		{
			final var catapult = level().getBlockEntity(blockPosition(), PortalCubedBlocks.CATAPULT_BLOCK_ENTITY);
			if (catapult.isPresent()) {
				((EntityExt)this).collidedWithCatapult(catapult.get());
				vel = getDeltaMovement();
			}
		}
		move(MoverType.SELF, vel);
		hasImpulse = true;
		int life = getLife();
		if (life > 0) {
			setLife(--life);
		} else if (life == 0) {
			kill(null);
		} // life < 0 means green pellet
		if (tickCount == 1) {
			level().playSound(null, position().x, position().y, position().z, PortalCubedSounds.PELLET_SPAWN_EVENT, SoundSource.HOSTILE, 1f, 1f);
		}
		Direction bouncedDir = null;
		if (verticalCollision) {
			vel = vel.with(Direction.Axis.Y, -vel.y);
			bouncedDir = vel.y < 0 ? Direction.DOWN : Direction.UP;
		}
		if (horizontalCollision) {
			if (getDeltaMovement().x == 0) {
				vel = vel.with(Direction.Axis.X, -vel.x);
				bouncedDir = vel.x < 0 ? Direction.WEST : Direction.EAST;
			}
			if (getDeltaMovement().z == 0) {
				vel = vel.with(Direction.Axis.Z, -vel.z);
				bouncedDir = vel.z < 0 ? Direction.NORTH : Direction.SOUTH;
			}
		}
		setDeltaMovement(vel);
		if (bouncedDir != null) {
			bounced();
			if (level() instanceof ServerLevel serverLevel) {
				final Vec3 spawnPos = serverLevel.clip(new ClipContext(
					position(),
					position().add(vel.with(bouncedDir.getAxis(), -vel.get(bouncedDir.getAxis()))),
					ClipContext.Block.COLLIDER,
					ClipContext.Fluid.NONE,
					this
				)).getLocation().add(Vec3.atLowerCornerOf(bouncedDir.getNormal()).scale(0.01));
				serverLevel.sendParticles(
					new DecalParticleOption(DecalParticleOption.SCORCH, bouncedDir),
					spawnPos.x, spawnPos.y, spawnPos.z,
					0, 0, 0, 0, 0
				);
			}
		}
		if ((tickCount - 1) % 34 == 0) {
			level().playSound(null, this, PortalCubedSounds.PELLET_TRAVEL_EVENT, SoundSource.HOSTILE, 0.4f, 1f);
		}
		final HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHit);
		if (hit.getType() == HitResult.Type.ENTITY) {
			bounceOrKill((LivingEntity)((EntityHitResult)hit).getEntity());
		} else {
			final LivingEntity hit2 = level().getNearestEntity(
				LivingEntity.class,
				TargetingConditions.forNonCombat().selector(this::canHit),
				null, getX(), getY(), getZ(),
				getBoundingBox()
			);
			if (hit2 != null) {
				bounceOrKill(hit2);
			}
		}
	}

	protected boolean canHit(Entity entity) {
		return entity instanceof LivingEntity && !entity.isSpectator() && entity.isAlive() && entity.isPickable();
	}

	private static double getBounceAngle(double inAngle, double propAngle) {
		// -(a - b) - 180 + b
		// Simplifies to
		// -a + b - 180 + b
		// -a - 180 + 2b
		return Mth.wrapDegrees(-inAngle - 180 + 2 * propAngle);
	}

	private void bounceOrKill(LivingEntity entity) {
		if (entity instanceof CorePhysicsEntity) {
			final Vec3 vel = getDeltaMovement();
			final double newAngle = Math.toRadians(getBounceAngle(
				Math.toDegrees(Math.atan2(vel.z, vel.x)),
				Mth.wrapDegrees(entity.getYRot() + 90)
			));
			final double mag = vel.length();
			setDeltaMovement(Math.cos(newAngle) * mag, vel.y, Math.sin(newAngle) * mag);
			level().playSound(null, this, PortalCubedSounds.PELLET_BOUNCE_EVENT, SoundSource.HOSTILE, 0.4f, 1f);
			bounced();
		} else {
			kill(entity);
		}
	}

	private void bounced() {
		level().playSound(null, this, PortalCubedSounds.PELLET_BOUNCE_EVENT, SoundSource.HOSTILE, 0.4f, 1f);
		bounces++;
		if (level() instanceof ServerLevel serverLevel && thrower != Util.NIL_UUID) {
			final ServerPlayer player = (ServerPlayer)serverLevel.getPlayerByUUID(thrower);
			if (player != null) {
				PortalCubedTriggers.BOUNCE.trigger(player, this);
			}
		}
	}

	private void kill(@Nullable LivingEntity entity) {
		level().playSound(null, position().x, position().y, position().z, PortalCubedSounds.PELLET_EXPLODE_EVENT, SoundSource.HOSTILE, 0.8f, 1f);
		if (entity != null) {
			entity.hurt(pcSources(level()).vaporization(this, getThrower()), PortalCubedConfig.pelletDamage);
		}
		kill();
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}

	@NotNull
	@Override
	public ItemStack getItem() {
		return new ItemStack(getLife() < 0 ? PortalCubedItems.SUPER_PELLET : PortalCubedItems.ENERGY_PELLET);
	}

	@Override
	public void wentThroughPortal(Portal portal) {
		setLife(getStartingLife());
	}

	public int getBounces() {
		return bounces;
	}

	public UUID getThrowerUUID() {
		return thrower;
	}

	@Nullable
	public Entity getThrower() {
		if (thrower == Util.NIL_UUID) {
			return null;
		}
		return ((LevelExt)level()).getEntityByUuid(thrower);
	}

	public void setThrower(Entity thrower) {
		this.thrower = thrower.getUUID();
	}
}
