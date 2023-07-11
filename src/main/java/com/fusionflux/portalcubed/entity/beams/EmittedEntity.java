package com.fusionflux.portalcubed.entity.beams;

import java.util.List;
import java.util.function.Consumer;

import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.quiltmc.qsl.entity.networking.api.extended_spawn_data.QuiltExtendedSpawnDataEntity;

public abstract class EmittedEntity extends Entity implements QuiltExtendedSpawnDataEntity {
	public static final EntityTypeTest<Entity, EmittedEntity> TYPE_TEST = EntityTypeTest.forClass(EmittedEntity.class);
	public static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(EmittedEntity.class, EntityDataSerializers.DIRECTION);
	public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(EmittedEntity.class, EntityDataSerializers.FLOAT);

	private Direction facing = Direction.NORTH;
	private float length = 1;
	private Vec3 center = Vec3.ZERO;
	private AABB listeningArea = new AABB(BlockPos.ZERO);

	public Consumer<EmittedEntity> modelUpdater = entity -> {};

	private final float maxLength;

	public EmittedEntity(EntityType<?> entityType, Level level, float maxLength) {
		super(entityType, level);
		this.maxLength = maxLength;
		setBoundingBox(makeBoundingBox());
	}

	public void setFacing(Direction facing) {
		entityData.set(FACING, facing);
	}

	public Direction getFacing() {
		// null on initial load during super call
		return facing == null ? Direction.NORTH : facing;
	}

	public void setLength(float length) {
		entityData.set(LENGTH, length);
	}

	public float getLength() {
		return length;
	}

	public Vec3 getCenter() {
		return center;
	}

	public boolean listensTo(BlockPos pos) {
		return isAlive() && listeningArea.contains(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	protected abstract AABB makeBaseBoundingBox();

	@Override
	@NotNull
	protected AABB makeBoundingBox() {
		AABB base = makeBaseBoundingBox();
		Vec3 pos = position();
		Vec3 offset = pos.relative(getFacing(), getLength()).subtract(pos); // relative offset along facing by length
		this.center = base.getCenter();
		AABB bounds = base.expandTowards(offset);
		Direction facing = getFacing();
		Vec3 facingNormal = Vec3.ZERO.with(facing.getAxis(), facing.getAxisDirection().getStep());
		this.listeningArea = bounds.expandTowards(facingNormal);
		return bounds;
	}

	public void reEmit() {
		Axis facingAxis = facing.getAxis();
		AABB bounds = makeBaseBoundingBox();
		// relative offset along facing by maxLength
		Vec3 offset = Vec3.ZERO.with(facingAxis, maxLength * facing.getAxisDirection().getStep());
		// how far the hitbox can actually move
		Vec3 actualOffset = Entity.collideBoundingBox(null, offset, bounds, level(), List.of());
		double size = facingAxis.choose(bounds.getXsize(), bounds.getYsize(), bounds.getZsize());
		float length = (float) (actualOffset.length() + size / 2);
		setLength(length);
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
		setFacing(NbtHelper.readEnum(tag, "facing", Direction.NORTH));
		if (tag.contains("length", Tag.TAG_FLOAT))
			setLength(tag.getFloat("length"));
	}

	private void updateBounds() {
		setBoundingBox(makeBoundingBox());
		modelUpdater.accept(this);
	}
}
