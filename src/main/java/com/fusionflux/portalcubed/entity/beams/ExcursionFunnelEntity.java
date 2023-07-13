package com.fusionflux.portalcubed.entity.beams;

import java.util.List;

import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.client.render.entity.model.ExcursionFunnelModel;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.LateRenderedEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.ExcursionFunnelEnterSoundInstance;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.quiltmc.loader.api.minecraft.ClientOnly;

public class ExcursionFunnelEntity extends EmittedEntity implements LateRenderedEntity {
	public static final EntityDataAccessor<Boolean> REVERSED = SynchedEntityData.defineId(ExcursionFunnelEntity.class, EntityDataSerializers.BOOLEAN);

	public static final float SIZE = (30 / 32f) * 2;

	private boolean reversed;

	@ClientOnly
	public ExcursionFunnelModel model;

	public ExcursionFunnelEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public static ExcursionFunnelEntity spawnAndEmit(Level level, Vec3 pos, Direction facing, boolean reversed, float length) {
		ExcursionFunnelEntity entity = new ExcursionFunnelEntity(PortalCubedEntities.EXCURSION_FUNNEL, level);
		entity.setPos(pos);
		entity.setFacing(facing);
		entity.setReversed(reversed);

		entity.reEmit(length);
		level.addFreshEntity(entity);
		return entity;
	}

	@Override
	protected EmittedEntity createNext() {
		ExcursionFunnelEntity entity = new ExcursionFunnelEntity(PortalCubedEntities.EXCURSION_FUNNEL, level());
		entity.setReversed(isReversed());
		return entity;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		entityData.set(REVERSED, reversed);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(REVERSED, false);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(REVERSED)) {
			this.reversed = entityData.get(REVERSED);
		}
	}

	@Override
	public void writeAdditionalSpawnData(FriendlyByteBuf buf) {
		super.writeAdditionalSpawnData(buf);
		buf.writeBoolean(reversed);
	}

	@Override
	public void readAdditionalSpawnData(FriendlyByteBuf buf) {
		super.readAdditionalSpawnData(buf);
		this.reversed = buf.readBoolean();
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("reversed", reversed);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setReversed(tag.getBoolean("reversed"));
	}

	@Override
	protected AABB makeBaseBoundingBox() {
		Direction facing = this.getFacing();
		Axis axis = facing.getAxis();
		Vec3 pos = position();
		return AABB.ofSize(pos, axis.choose(0, SIZE, SIZE), axis.choose(SIZE, 0, SIZE), axis.choose(SIZE, SIZE, 0));
	}

	@Override
	public void tick() {
		super.tick();
		Level level = level();
		List<LivingEntity> colliding = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox(), Entity::isAlive);
		if (!colliding.isEmpty()) {
			Direction facing = getFacing();
			Direction motion = isReversed() ? facing.getOpposite() : facing;
			Vec3 center = getCenter();
			for (LivingEntity entity : colliding) {
				applyEffects(entity, center, motion);
			}
		}
	}

	public static void applyEffects(Entity entity, Vec3 tubeCenter, Direction motionDirection) {
		if (entity instanceof Player player && player.getAbilities().flying)
			return;
		Vec3 entityCenter = entity.getBoundingBox().getCenter();
		Vec3 motion = Vec3.atLowerCornerOf(motionDirection.getNormal()).scale(0.125);

		RayonIntegration.INSTANCE.setNoGravity(entity, true);
		entity.resetFallDistance();

		EntityExt entityEx = (EntityExt) entity;
		if (!entityEx.isInFunnel()) {
			entityEx.setInFunnel(true);
			entity.setDeltaMovement(0, 0, 0);
			if (entity instanceof Player player && player.isLocalPlayer())
				playEnterSound();
		}
		entityEx.setFunnelTimer(2);

		Vec3 velocity = entity.getDeltaMovement();
		// check for inputs
		if (entity instanceof HasMovementInputAccessor inputProvider && inputProvider.hasMovementInputPublic()) {
			if (motion.x == 0)
				motion = motion.add(velocity.x, 0, 0);
			if (motion.y == 0)
				motion = motion.add(0, velocity.y, 0);
			if (motion.z == 0)
				motion = motion.add(0, 0, velocity.z);
		}

		// move entity towards center
		double dx = entityCenter.x - tubeCenter.x + velocity.x;
		double dy = entityCenter.y - tubeCenter.y + velocity.y;
		double dz = entityCenter.z - tubeCenter.z + velocity.z;

		if (motion.x == 0)
			motion = motion.add(-Math.copySign(Math.sqrt(Math.abs(dx)), dx) / 20, 0, 0);
		if (motion.y == 0)
			motion = motion.add(0, -Math.copySign(Math.sqrt(Math.abs(dy)), dy) / 20, 0);
		if (motion.z == 0)
			motion = motion.add(0, 0, -Math.copySign(Math.sqrt(Math.abs(dz)), dz) / 20);

		entity.setDeltaMovement(motion);

		if (entity.isShiftKeyDown() && motion.lengthSqr() < 0.15 * 0.15 && !entity.isFree(motion.x, motion.y, motion.z)) {
			entityEx.setCFG();
		}
	}

	@ClientOnly
	private static void playEnterSound() {
		Minecraft.getInstance().getSoundManager().play(new ExcursionFunnelEnterSoundInstance());
	}
}
