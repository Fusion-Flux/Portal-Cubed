package com.fusionflux.portalcubed.entity.beams;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.PortalListeningEntity;
import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.quiltmc.qsl.entity.networking.api.extended_spawn_data.QuiltExtendedSpawnDataEntity;

public abstract class EmittedEntity extends PortalListeningEntity implements QuiltExtendedSpawnDataEntity {
	public static final int MAX_LENGTH = 100;
	public static final EntityTypeTest<Entity, EmittedEntity> TYPE_TEST = EntityTypeTest.forClass(EmittedEntity.class);
	public static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(EmittedEntity.class, EntityDataSerializers.DIRECTION);
	public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(EmittedEntity.class, EntityDataSerializers.FLOAT);

	private Direction facing = Direction.NORTH;
	private float length = 1;
	private float targetLength = 1;
	private Vec3 center = Vec3.ZERO;
	private AABB listeningArea = new AABB(BlockPos.ZERO);
	private int reEmitTimer;

	// UUID of next in line. resolving this will always work, since the first entity
	// is the only one that can be unloaded (others are chunk loaded by portals)
	@Nullable private UUID next;
	// portal this is being emitted from
	@Nullable private UUID sourcePortal;

	public Consumer<EmittedEntity> modelUpdater = entity -> {};

	public EmittedEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
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

	protected abstract EmittedEntity createNext();

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
		reEmit(targetLength);
	}

	public void reEmit(float maxLength) {
		removeNextEntity(); // remove existing emitted entities
		this.targetLength = maxLength;
		Level level = level();
		Direction facing = getFacing();
		Axis facingAxis = facing.getAxis();
		AABB bounds = makeBaseBoundingBox();
		// relative offset along facing by maxLength
		Vec3 offset = Vec3.ZERO.with(facingAxis, maxLength * facing.getAxisDirection().getStep());
		// how far the hitbox can actually move
		Vec3 actualOffset = Entity.collideBoundingBox(null, offset, bounds, level, List.of());
		double size = facingAxis.choose(bounds.getXsize(), bounds.getYsize(), bounds.getZsize());
		float length = (float) (actualOffset.length() + size / 2);
		setLength(length);

		if (maxLength - length > 0.1) { // don't bother with going through portals if too short
			AABB portalArea = bounds.move(actualOffset).expandTowards(actualOffset.normalize());
			List<Portal> portals = level.getEntities(PortalCubedEntities.PORTAL, portalArea, this::isPortalAligned);
			if (!portals.isEmpty()) {
				if (portals.size() != 1) { // prefer nearest portal when multiple present
					portals.sort(Comparator.comparingDouble(this::distanceToSqr));
				}

				Portal portal = portals.get(0);
				portal.addListener(this);

				Portal linked = portal.findLinkedPortal();
				if (linked != null) { // if already active, emit through
					Vector2d planeCoords = portal.getLocalPlaneCoords(position());
					Vec3 otherSidePos = linked.getPointInPlane(-planeCoords.x, planeCoords.y); // -x: mirror
					Direction otherFacing = linked.getFacingDirection();

					EmittedEntity nextEntity = createNext();
					nextEntity.setPos(otherSidePos);
					nextEntity.setFacing(otherFacing);
					nextEntity.reEmit(maxLength - length);
					nextEntity.sourcePortal = linked.getUUID();
					linked.addListener(nextEntity);
					level.addFreshEntity(nextEntity);

					this.next = nextEntity.getUUID();
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		reEmitTimer--;
		if (reEmitTimer == 0) {
			reEmit();
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		if (reason.shouldDestroy())
			removeNextEntity();
	}

	private void removeNextEntity() {
		if (next != null && level() instanceof ServerLevel level) {
			Entity next = level.getEntity(this.next);
			if (next != null) {
				next.remove(RemovalReason.DISCARDED);
			}
			this.next = null;
		}
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
		tag.putFloat("targetLength", targetLength);
		if (sourcePortal != null)
			tag.putUUID("sourcePortal", sourcePortal);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		setFacing(NbtHelper.readEnum(tag, "facing", Direction.NORTH));
		if (tag.contains("length", Tag.TAG_FLOAT))
			setLength(tag.getFloat("length"));
		if (tag.contains("targetLength", Tag.TAG_FLOAT))
			this.targetLength = tag.getFloat("targetLength");
		if (tag.contains("sourcePortal", Tag.TAG_INT_ARRAY))
			this.sourcePortal = tag.getUUID("sourcePortal");
	}

	private void updateBounds() {
		setBoundingBox(makeBoundingBox());
		modelUpdater.accept(this);
	}

	public boolean isPortalAligned(Portal portal) {
		return portal.isAlive() && getFacing().getOpposite() == portal.getFacingDirection();
	}

	@Override
	public void onPortalCreate(Portal portal) {
		if (getFacing().getOpposite() == portal.getFacingDirection())
			reEmitTimer = 3; // direction aligns, try to emit through (next tick, portal not ready)
	}

	@Override
	public void onPortalRemove(Portal portal) {
		if (Objects.equals(sourcePortal, portal.getUUID())) {
			discard(); // source removed
		} else {
			removeNextEntity(); // going into the portal, remove on other side
		}
	}

	@Override
	public void onLinkedPortalCreate(Portal portal, Portal linked) {
		reEmitTimer = 3;
	}
}
