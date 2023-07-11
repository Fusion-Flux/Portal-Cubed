package com.fusionflux.portalcubed.entity.beams;

import java.util.List;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.client.render.entity.model.ExcursionFunnelModel;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.ExcursionFunnelEnterSoundInstance;
import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.entity.networking.api.extended_spawn_data.QuiltExtendedSpawnDataEntity;

public class ExcursionFunnelEntity extends Entity implements QuiltExtendedSpawnDataEntity {
	public static final ResourceLocation MODEL = PortalCubed.id("entity/excursion_funnel_beam_forward");
	public static final ResourceLocation REVERSED_MODEL = PortalCubed.id("entity/excursion_funnel_beam_reversed");
	public static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(ExcursionFunnelEntity.class, EntityDataSerializers.DIRECTION);
	public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(ExcursionFunnelEntity.class, EntityDataSerializers.FLOAT);
	public static final float SIZE = (30 / 32f) * 2;
	private Vec3 center = Vec3.ZERO;

	public Direction facing = Direction.NORTH;
	public float length = 1;

	@ClientOnly
	public ExcursionFunnelModel model;

	public ExcursionFunnelEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		setBoundingBox(makeBoundingBox());
	}

	// can't be a constructor, generics explode
	public static ExcursionFunnelEntity create(ServerLevel level, Direction facing, float length) {
		ExcursionFunnelEntity entity = new ExcursionFunnelEntity(PortalCubedEntities.EXCURSION_FUNNEL, level);
		entity.entityData.set(FACING, facing);
		entity.onSyncedDataUpdated(FACING);
		entity.entityData.set(LENGTH, length);
		entity.onSyncedDataUpdated(LENGTH);
		return entity;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FACING, Direction.NORTH);
		entityData.define(LENGTH, 1f);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (FACING.equals(key)) {
			this.facing = entityData.get(FACING);
			updateBounds();
		} else if (LENGTH.equals(key)) {
			this.length = entityData.get(LENGTH);
			updateBounds();
		}
	}

	@Override
	public void writeAdditionalSpawnData(FriendlyByteBuf buf) {
		buf.writeEnum(facing);
		buf.writeFloat(length);
	}

	@Override
	public void readAdditionalSpawnData(FriendlyByteBuf buf) {
		this.facing = buf.readEnum(Direction.class);
		this.length = buf.readFloat();
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putString("facing", facing.getSerializedName());
		tag.putFloat("length", length);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		Direction facing = NbtHelper.readEnum(tag, "facing", Direction.NORTH);
		entityData.set(FACING, facing);
		if (tag.contains("length", Tag.TAG_FLOAT))
			entityData.set(LENGTH, tag.getFloat("length"));
	}

	public ResourceLocation getModel() {
		return MODEL;
	}

	@Override
	@NotNull
	protected AABB makeBoundingBox() {
		// null on initial load during super call
		Direction facing = this.facing == null ? Direction.NORTH : this.facing;
		Axis axis = facing.getAxis();
		Vec3 pos = position();
		AABB base = AABB.ofSize(pos, axis.choose(0, SIZE, SIZE), axis.choose(SIZE, 0, SIZE), axis.choose(SIZE, SIZE, 0));
		Vec3 offset = pos.relative(facing, length).subtract(pos); // relative offset along facing by length
		AABB bounds = base.expandTowards(offset);
		this.center = bounds.getCenter();
		return bounds;
	}

	private void updateBounds() {
		setBoundingBox(makeBoundingBox());
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		Level level = level();
		List<LivingEntity> colliding = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox(), Entity::isAlive);
		if (!colliding.isEmpty()) {
			Direction facing = entityData.get(FACING);
			for (LivingEntity entity : colliding) {
				applyEffects(entity, center, facing);
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
