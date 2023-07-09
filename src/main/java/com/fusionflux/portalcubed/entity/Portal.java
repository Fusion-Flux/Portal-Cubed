package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.PortalMoveListeningBlock;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class Portal extends Entity {

    public static final Supplier<IllegalStateException> NOT_INIT =
        () -> new IllegalStateException("Portal data accessed before initialized");

    private static final IPQuaternion FLIP_AXIS_W = IPQuaternion.rotationByDegrees(
        new Vec3(0, 1, 0), 180
    ).fixFloatingPointErrorAccumulation();

    public static final AABB NULL_BOX = new AABB(0, 0, 0, 0, 0, 0);

    public static final double SURFACE_OFFSET = 0.01;

    private static final double WIDTH = 0.9, HEIGHT = 1.9;
    private static final double EPSILON = 1.0E-7;

    private static final Vec3 AXIS_W = new Vec3(1, 0, 0);
    private static final Vec3 AXIS_H = new Vec3(0, -1, 0);
    private static final Vec3 NORMAL = new Vec3(0, 0, -1);

    private AABB cutoutBoundingBox = NULL_BOX;

    private static final EntityDataAccessor<LerpedQuaternion> ROTATION = SynchedEntityData.defineId(Portal.class, PortalCubedTrackedDataHandlers.LERPED_QUAT);
    private static final EntityDataAccessor<Optional<Quaternionf>> OTHER_ROTATION = SynchedEntityData.defineId(Portal.class, PortalCubedTrackedDataHandlers.OPTIONAL_QUAT);
    public static final EntityDataAccessor<Optional<UUID>> LINKED_PORTAL_UUID = SynchedEntityData.defineId(Portal.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Boolean> IS_ACTIVE = SynchedEntityData.defineId(Portal.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Portal.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Optional<Vec3>> DESTINATION = SynchedEntityData.defineId(Portal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(Portal.class, EntityDataSerializers.OPTIONAL_UUID);

    private boolean disableValidation = false;
    private Vec3 axisW, axisH, normal;
    private Optional<Vec3> otherAxisW = Optional.empty(), otherAxisH = Optional.empty(), otherNormal = Optional.empty();

    private VoxelShape crossCollisionThis;
    private long crossCollisionThisTick = -1;

    public Portal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ROTATION, new LerpedQuaternion(new Quaternionf()).withUpdateCallback(this::onRotationUpdate));
        this.getEntityData().define(OTHER_ROTATION, Optional.empty());
        this.getEntityData().define(LINKED_PORTAL_UUID, Optional.empty());
        this.getEntityData().define(IS_ACTIVE, false);
        this.getEntityData().define(COLOR, 0);
        this.getEntityData().define(DESTINATION, Optional.empty());
        this.getEntityData().define(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.setColor(nbt.getInt("color"));
        setRotation(LerpedQuaternion.fromNbt(nbt.getCompound("PortalRotation")));
        if (nbt.contains("OtherRotation")) {
            setOtherRotation(Optional.of(NbtHelper.getQuaternion(nbt, "OtherRotation")));
        } else {
            setOtherRotation(Optional.empty());
        }
        if (nbt.hasUUID("linkedPortalUUID")) this.setLinkedPortalUUID(Optional.of(nbt.getUUID("linkedPortalUUID")));
        if (nbt.contains("destination")) this.setDestination(Optional.of(NbtHelper.getVec3d(nbt, "destination")));
        if (nbt.hasUUID("ownerUUID")) this.setOwnerUUID(Optional.of(nbt.getUUID("ownerUUID")));
        disableValidation = nbt.getBoolean("DisableValidation");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("color", this.getColor());
        nbt.put("PortalRotation", getRotation().toNbt());
        getOtherRotation().ifPresent(r -> NbtHelper.putQuaternion(nbt, "OtherRotation", r));
        this.getLinkedPortalUUID().ifPresent(uuid -> nbt.putUUID("linkedPortalUUID", uuid));
        this.getDestination().ifPresent(destination -> NbtHelper.putVec3d(nbt, "destination", destination));
        this.getOwnerUUID().ifPresent(uuid -> nbt.putUUID("ownerUUID", uuid));
        nbt.putBoolean("DisableValidation", disableValidation);
    }

    public int getColor() {
        return getEntityData().get(COLOR);
    }

    public void setColor(int color) {
        this.getEntityData().set(COLOR, color);
    }

    public Optional<UUID> getLinkedPortalUUID() {
        return getEntityData().get(LINKED_PORTAL_UUID);
    }

    public void setLinkedPortalUUID(Optional<UUID> uuid) {
        this.getEntityData().set(LINKED_PORTAL_UUID, uuid);
    }

    public Optional<UUID> getOwnerUUID() {
        return getEntityData().get(OWNER_UUID);
    }

    public void setOwnerUUID(Optional<UUID> uuid) {
        this.getEntityData().set(OWNER_UUID, uuid);
    }

    public boolean getActive() {
        return getEntityData().get(IS_ACTIVE);
    }

    private void setActive(boolean active) {
        this.getEntityData().set(IS_ACTIVE, active);
    }

    /**
     * Unless you <i>need</i> axis alignment, this method should be avoided, and methods based off of
     * {@link #getRotation()} or {@link #getNormal()} should be used instead.
     * @return The closest direction to the portal's rotation
     * @see #getRotation()
     * @see #getNormal()
     */
    public Direction getFacingDirection() {
        final Vec3 normal = getNormal();
        final double x = normal.x, y = normal.y, z = normal.z;
        final Direction result = Direction.fromDelta((int)x, (int)y, (int)z);
        return result != null ? result : Direction.getNearest(x, y, z);
    }

    public LerpedQuaternion getRotation() {
        return getEntityData().get(ROTATION);
    }

    public void setRotation(Quaternionf rotation) {
        setRotation(new LerpedQuaternion(rotation));
    }

    public void setRotation(LerpedQuaternion rotation) {
        getEntityData().set(ROTATION, rotation.withUpdateCallback(this::onRotationUpdate));
    }

    public Optional<Quaternionf> getOtherRotation() {
        return getEntityData().get(OTHER_ROTATION);
    }

    public void setOtherRotation(Optional<Quaternionf> rotation) {
        getEntityData().set(OTHER_ROTATION, rotation);
    }

    private Vec3 applyAxis(Vec3 axis) {
        return IPQuaternion.fromQuaternionf(getRotation().get()).rotate(axis, true);
    }

    public Vec3 getAxisW() {
        return axisW == null ? (axisW = applyAxis(AXIS_W)) : axisW;
    }

    public Vec3 getAxisH() {
        return axisH == null ? (axisH = applyAxis(AXIS_H)) : axisH;
    }

    public Vec3 getNormal() {
        return normal == null ? (normal = applyAxis(NORMAL)) : normal;
    }

    private Optional<Vec3> applyOtherAxis(Vec3 axis) {
        return getOtherRotation().map(r -> IPQuaternion.fromQuaternionf(r).rotate(axis, true));
    }

    public Optional<Vec3> getOtherAxisW() {
        return otherAxisW.isEmpty() ? (otherAxisW = applyOtherAxis(AXIS_W)) : otherAxisW;
    }

    public Optional<Vec3> getOtherAxisH() {
        return otherAxisH.isEmpty() ? (otherAxisH = applyOtherAxis(AXIS_H)) : otherAxisH;
    }

    public Optional<Vec3> getOtherNormal() {
        return otherNormal.isEmpty() ? (otherNormal = applyOtherAxis(NORMAL)) : otherNormal;
    }

    public Optional<Vec3> getDestination() {
        return getEntityData().get(DESTINATION);
    }

    public void setDestination(Optional<Vec3> destination) {
        this.getEntityData().set(DESTINATION, destination);
    }

    @Nullable
    public Portal findLinkedPortal() {
        if (!(level() instanceof ServerLevel level))
            return null;
        Optional<UUID> linkedId = getLinkedPortalUUID();
        if (linkedId.isEmpty())
            return null;
        Entity linkedEntity = level.getEntity(linkedId.get());
        return linkedEntity instanceof Portal linked ? linked : null;
    }

    @Override
    public void kill() {
        notifyListeners(true);
        getOwnerUUID().ifPresent(uuid -> {
            Entity player = ((ServerLevel) level()).getEntity(uuid);
            CalledValues.removePortals(player, this.getUUID());
        });
        super.kill();
    }

    @Override
    public void tick() {
        final ProfilerFiller profiler = level().getProfiler();
        profiler.push("portalTick");

        this.makeBoundingBox();
        this.calculateCutoutBox();

        getRotation().tick();

        if (!this.level().isClientSide) {
            final ServerLevel serverLevel = (ServerLevel)level();
            serverLevel.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPosition(), 2, blockPosition());

            getOwnerUUID().ifPresent(uuid -> {
                Entity player = serverLevel.getEntity(uuid);
                if (player == null || !player.isAlive()) {
                    this.kill();
                }
            });

            Portal otherPortal =
                this.getLinkedPortalUUID().isPresent()
                    ? (Portal)serverLevel.getEntity(this.getLinkedPortalUUID().get())
                    : null;

            setActive(otherPortal != null);
            setDestination(Optional.of(Objects.requireNonNullElse(otherPortal, this).getOriginPos()));

            if (!validate()) {
                this.kill();
                level().playSound(null, getX(), getY(), getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundSource.NEUTRAL, .1F, 1F);
            }

        }

        profiler.pop();
        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ROTATION.equals(key)) {
            axisW = axisH = normal = null;
            makeBoundingBox();
        } else if (OTHER_ROTATION.equals(key)) {
            otherAxisW = otherAxisH = otherNormal = Optional.empty();
        }
    }

    public void setDisableValidation(boolean disableValidation) {
        this.disableValidation = disableValidation;
    }

    public boolean validate() {
        if (disableValidation) {
            return true;
        }
        return validateBehind() && validateFront();
    }

    private boolean validateBehind() {
        final AABB portalBox = new AABB(
            getPointInPlane(width() / 2, height() / 2)
                .add(getNormal().scale(-0.01)),
            getPointInPlane(-width() / 2, -height() / 2)
                .add(getNormal().scale(-0.2))
        ).minmax(new AABB(
            getPointInPlane(-width() / 2, height() / 2)
                .add(getNormal().scale(-0.01)),
            getPointInPlane(width() / 2, -height() / 2)
                .add(getNormal().scale(-0.2))
        ));
        final Cursor3D iter = new Cursor3D(
            Mth.floor(portalBox.minX - EPSILON) - 1,
            Mth.floor(portalBox.minY - EPSILON) - 1,
            Mth.floor(portalBox.minZ - EPSILON) - 1,
            Mth.floor(portalBox.maxX + EPSILON) + 1,
            Mth.floor(portalBox.maxY + EPSILON) + 1,
            Mth.floor(portalBox.maxZ + EPSILON) + 1
        );
        Direction forward = getFacingDirection();
        BooleanProperty coveringWall = MultifaceBlock.getFaceProperty(forward.getOpposite());
        Player owner = getOwnerUUID().map(level()::getPlayerByUUID).orElse(null);
        while (iter.advance()) {
            final BlockPos pos = new BlockPos(iter.nextX(), iter.nextY(), iter.nextZ());
            if (!AABB.of(BoundingBox.fromCorners(pos, pos)).intersects(portalBox)) continue;

            BlockState wall = level().getBlockState(pos);
            BlockState facade = level().getBlockState(pos.relative(forward));
            BlockState portalSurface;
            if (!facade.is(PortalCubedBlocks.PORTAL_NONSOLID) && // non-solids fallback to the wall
                    facade.getOptionalValue(coveringWall).orElse(Boolean.FALSE)) { // if property is present and true, facade covers the wall
                if (!facade.is(PortalCubedBlocks.PORTALABLE_GELS))
                    return false; // cannot support portals
                portalSurface = facade;
            } else { // no facade, check the wall directly
                if (wall.is(PortalCubedBlocks.PORTAL_NONSOLID) || wall.is(PortalCubedBlocks.CANT_PLACE_PORTAL_ON))
                    return false; // cannot support portals
                portalSurface = wall;
            }
            if (owner != null && !owner.getAbilities().mayBuild && !owner.isSpectator()) { // finally, check if the surface is valid in adventure mode
                if (!portalSurface.is(PortalCubedBlocks.PORTALABLE_IN_ADVENTURE))
                    return false;
            }

            final VoxelShape shape = wall.getCollisionShape(level(), pos, CollisionContext.of(this));
            if (
                shape.move(pos.getX(), pos.getY(), pos.getZ())
                    .toAabbs()
                    .stream()
                    .noneMatch(portalBox::intersects)
            ) return false;
        }
        return true;
    }

    private boolean validateFront() {
        final AABB portalBox = new AABB(
            getPointInPlane(width() / 2, height() / 2)
                .add(getNormal().scale(0.2)),
            getPointInPlane(-width() / 2, -height() / 2)
        ).minmax(new AABB(
            getPointInPlane(-width() / 2, height() / 2)
                .add(getNormal().scale(0.2)),
            getPointInPlane(width() / 2, -height() / 2)
        ));
        final Cursor3D iter = new Cursor3D(
            Mth.floor(portalBox.minX - EPSILON) - 1,
            Mth.floor(portalBox.minY - EPSILON) - 1,
            Mth.floor(portalBox.minZ - EPSILON) - 1,
            Mth.floor(portalBox.maxX + EPSILON) + 1,
            Mth.floor(portalBox.maxY + EPSILON) + 1,
            Mth.floor(portalBox.maxZ + EPSILON) + 1
        );
        while (iter.advance()) {
            final BlockPos pos = new BlockPos(iter.nextX(), iter.nextY(), iter.nextZ());
            if (!AABB.of(BoundingBox.fromCorners(pos, pos)).intersects(portalBox)) continue;
            final BlockState state = level().getBlockState(pos);
            if (state.is(PortalCubedBlocks.PORTAL_NONSOLID)) continue;
            if (state.is(PortalCubedBlocks.PORTAL_SOLID)) {
                return false;
            }
            final VoxelShape shape = state.getCollisionShape(level(), pos, CollisionContext.of(this));
            if (
                shape.move(pos.getX(), pos.getY(), pos.getZ())
                    .toAabbs()
                    .stream()
                    .anyMatch(portalBox::intersects)
            ) return false;
        }
        return true;
    }

    private void onRotationUpdate(LerpedQuaternion quaternion) {
        getEntityData().set(ROTATION, quaternion.copy()); // update on the other side, server/client
    }

    public boolean isGridAligned() {
        return position().subtract(getGridAlignedPos()).lengthSqr() < EPSILON;
    }

    public boolean snapToGrid() {
        Vec3 newPos = getGridAlignedPos();
        Vec3 offset = newPos.subtract(position());
        if (offset.lengthSqr() < EPSILON)
            return true;
        AABB movedBounds = getBoundingBox().move(offset);
        List<Portal> intersecting = level().getEntitiesOfClass(Portal.class, movedBounds.inflate(0.1), portal -> portal != this);
        if (!intersecting.isEmpty())
            return false;
        setPos(newPos);
        return true;
    }

    private Vec3 getGridAlignedPos() {
        Direction facing = getFacingDirection();

        Axis forwards = facing.getAxis();
        Vec3 down = getRelativeDown();
        Axis vertical = Direction.getNearest(down.x, down.y, down.z).getAxis();
        Axis horizontal = Axis.X;
        // just find whichever hasn't been chosen yet
        for (Axis axis : Axis.VALUES) {
            if (axis != forwards && axis != vertical) {
                horizontal = axis;
                break;
            }
        }

        Vec3 pos = position();
        MutableVec3 newPos = new MutableVec3();
        for (Axis axis : Axis.VALUES) {
            if (axis == forwards) {  // forwards axis: leave alone, same offset from wall
                newPos.set(axis, pos.get(axis));
            } else if (axis == vertical) { // vertical axis: round to 0
                newPos.set(axis, Math.floor(pos.get(axis)));
            } else if (axis == horizontal) { // horizontal axis: round to 0.5
                newPos.set(axis, Math.floor(pos.get(axis)) + 0.5);
            }
        }
        return new Vec3(newPos.x, newPos.y, newPos.z);
    }

    public Vec3 getRelativeDown() {
        Vector3f transformed = getRotation().get().transform(new Vector3f(0, -1, 0));
        return new Vec3(transformed);
    }

    public void notifyListeners(boolean removed) {
        if (level() instanceof ServerLevel level) {
            Vec3 offset = Vec3.atBottomCenterOf(getFacingDirection().getNormal()).scale(0.5);
            AABB bounds = makeBoundingBox().move(offset).inflate(-0.1);
            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof PortalMoveListeningBlock listener) {
                    if (removed) {
                        listener.beforePortalRemove(level, state, pos, this);
                    } else {
                        listener.onPortalCreate(level, state, pos, this);
                    }
                }
            });
        }
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        AABB portalBox = new AABB(
                getPointInPlane(width() / 2, height() / 2)
                        .add(getNormal().scale(.0)),
                getPointInPlane(-width() / 2, -height() / 2)
                        .add(getNormal().scale(-.2))
        ).minmax(new AABB(
                getPointInPlane(-width() / 2, height() / 2)
                        .add(getNormal().scale(.0)),
                getPointInPlane(width() / 2, -height() / 2)
                        .add(getNormal().scale(-.2))
        ));
        setBoundingBox(portalBox);
        return portalBox;
    }

    public AABB calculateCutoutBox() {
        AABB portalBox = new AABB(
                getCutoutPointInPlane(width() / 2, height() / 2)
                        .add(getNormal().scale(5)),
                getCutoutPointInPlane(-width() / 2, -height() / 2)
                        .add(getNormal().scale(-5))
        ).minmax(new AABB(
                getCutoutPointInPlane(-width() / 2, height() / 2)
                        .add(getNormal().scale(5)),
                getCutoutPointInPlane(width() / 2, -height() / 2)
                        .add(getNormal().scale(-5))
        ));
        setCutoutBoundingBox(portalBox);
        return portalBox;
    }


    public AABB calculateBoundsCheckBox() {
        return new AABB(
                getBoundsCheckPointInPlane(width() / 2, height() / 2)
                        .add(getNormal().scale(2.5)),
                getBoundsCheckPointInPlane(-width() / 2, -height() / 2)
                        .add(getNormal().scale(-2.5))
        ).minmax(new AABB(
                getBoundsCheckPointInPlane(-width() / 2, height() / 2)
                        .add(getNormal().scale(2.5)),
                getBoundsCheckPointInPlane(width() / 2, -height() / 2)
                        .add(getNormal().scale(-2.5))
        ));
    }

    public final AABB getCutoutBoundingBox() {
        return this.cutoutBoundingBox;
    }

    public final void setCutoutBoundingBox(AABB boundingBox) {
        this.cutoutBoundingBox = boundingBox;
    }

    public VoxelShape getCrossPortalCollisionShapeOther(Entity context) {
        // getActive() returning true asserts that these parameters return present Optionals
        //noinspection OptionalGetWithoutIsPresent
        return getActive()
            ? calculateCrossPortalCollisionShape(getOtherNormal().get(), getDestination().get(), getOtherRotation().get(), context)
            : Shapes.empty();
    }

    public VoxelShape getCrossPortalCollisionShapeThis() {
        final long tick = level().getGameTime();
        if (crossCollisionThis == null || crossCollisionThisTick < tick) {
            crossCollisionThis = calculateCrossPortalCollisionShape(getNormal(), getOriginPos(), null, this);
            crossCollisionThisTick = tick;
        }
        return crossCollisionThis;
    }

    private VoxelShape calculateCrossPortalCollisionShape(Vec3 normal, Vec3 origin, Quaternionf otherRotation, Entity context) {
        origin = origin.subtract(normal.scale(SURFACE_OFFSET));
        final Direction facing = Direction.getNearest(normal.x, normal.y, normal.z);
        final AABB clipping = GeneralUtil.capAABBAt(
            origin.subtract(2, 2, 2),
            origin.add(2, 2, 2),
            facing, origin
        );
        final VoxelShape clippingShape = Shapes.create(clipping);
        VoxelShape result = Shapes.empty();
        for (final VoxelShape shape : level().getBlockCollisions(context, clipping)) {
            result = Shapes.or(result, Shapes.joinUnoptimized(shape, clippingShape, BooleanOp.AND));
        }
        if (otherRotation != null && !result.isEmpty() /* Empty shapes don't need to be translated */) {
            final Vec3 scaledNormalOffset = getNormal().scale(SURFACE_OFFSET);
            if (facing != getFacingDirection().getOpposite()) {
                result = result.move(-origin.x, -origin.y, -origin.z);
                final Vector3d rotationVec = getTransformQuat().toQuaterniond().getEulerAnglesZXY(new Vector3d());
                result = VoxelShaper.rotatedCopy(
                    result,
                    new Vec3(
                        Math.toDegrees(rotationVec.x),
                        -Math.toDegrees(rotationVec.y),
                        Math.toDegrees(rotationVec.z)
                    ),
                    Vec3.ZERO
                );
                result = result.move(
                    getX() - scaledNormalOffset.x,
                    getY() - scaledNormalOffset.y,
                    getZ() - scaledNormalOffset.z
                );
            } else {
                result = result.move(
                    getX() - origin.x - scaledNormalOffset.x,
                    getY() - origin.y - scaledNormalOffset.y,
                    getZ() - origin.z - scaledNormalOffset.z
                );
            }
        }
        return result;
    }

    public Vec3 getCutoutPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().step().x() * -5, getFacingDirection().step().y() * -5, getFacingDirection().step().z() * -5);
    }

    public Vec3 getBoundsCheckPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().step().x() * 2.5, getFacingDirection().step().y() * 2.5, getFacingDirection().step().z() * 2.5);
    }

    public Vec3 getPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane));
    }

    public Vec3 getPointInPlaneLocal(double xInPlane, double yInPlane) {
        return getAxisW().scale(xInPlane).add(getAxisH().scale(yInPlane));
    }

    public Vec3 getOriginPos() {
        return position();
    }

    public void setOriginPos(Vec3 pos) {
        setPos(pos);
    }

    private double width() {
        return WIDTH * PehkuiScaleTypes.HITBOX_WIDTH.getScaleData(this).getScale();
    }

    private double height() {
        return HEIGHT * PehkuiScaleTypes.HITBOX_HEIGHT.getScaleData(this).getScale();
    }

    public IPQuaternion getTransformQuat() {
        final IPQuaternion myRotation = IPQuaternion.fromQuaternionf(getRotation().get());
        final IPQuaternion otherRotation = IPQuaternion.fromQuaternionf(getOtherRotation().orElseThrow(NOT_INIT));
        return otherRotation
            .hamiltonProduct(FLIP_AXIS_W)
            .hamiltonProduct(myRotation.getConjugated());
    }

}
