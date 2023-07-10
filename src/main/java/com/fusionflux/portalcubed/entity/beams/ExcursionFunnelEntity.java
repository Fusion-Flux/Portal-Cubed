package com.fusionflux.portalcubed.entity.beams;

import java.util.List;

import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExcursionFunnelEntity extends Entity {
	public static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(ExcursionFunnelEntity.class, EntityDataSerializers.DIRECTION);
	public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(ExcursionFunnelEntity.class, EntityDataSerializers.FLOAT);
	public static final float SIZE = 30 / 32f;
	private Vec3 motion = Vec3.ZERO;

	public ExcursionFunnelEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	// can't be a constructor, generics explode
	public static ExcursionFunnelEntity create(ServerLevel level, Direction facing, float length) {
		ExcursionFunnelEntity entity = new ExcursionFunnelEntity(PortalCubedEntities.EXCURSION_FUNNEL, level);
		entity.entityData.set(FACING, facing);
		entity.entityData.set(LENGTH, length);
		return entity;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FACING, Direction.NORTH);
		entityData.define(LENGTH, 1f);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		tag.putString("facing", entityData.get(FACING).getSerializedName());
		tag.putFloat("length", entityData.get(LENGTH));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		Direction facing = NbtHelper.readEnum(tag, "facing", Direction.NORTH);
		entityData.set(FACING, facing);
		if (tag.contains("length", Tag.TAG_FLOAT))
			entityData.set(LENGTH, tag.getFloat("length"));
	}

	@Override
	@NotNull
	protected AABB makeBoundingBox() {
		Direction facing = entityData.get(FACING);
		Axis axis = facing.getAxis();
		float length = entityData.get(LENGTH);
		Vec3 pos = position();
		AABB base = AABB.ofSize(pos, axis.choose(0, SIZE, SIZE), axis.choose(SIZE, 0, SIZE), axis.choose(SIZE, SIZE, 0));
		Vec3 offset = pos.relative(facing, length).subtract(pos); // relative offset along facing by length
		return base.expandTowards(offset);
	}

	@Override
	public void tick() {
		super.tick();
		Level level = level();
		List<LivingEntity> colliding = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox(), Entity::isAlive);
		for (LivingEntity entity : colliding) {
			entity.setDeltaMovement(motion);
		}
	}
}
