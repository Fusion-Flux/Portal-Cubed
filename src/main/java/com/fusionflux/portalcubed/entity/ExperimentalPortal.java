package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPHelperDuplicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class  ExperimentalPortal extends Entity {

    public static final Supplier<IllegalStateException> NOT_INIT =
        () -> new IllegalStateException("Portal data accessed before initialized");

    private static final AABB NULL_BOX = new AABB(0, 0, 0, 0, 0, 0);

    private static final double WIDTH = 0.9, HEIGHT = 1.9;
    private static final double EPSILON = 1.0E-7;

    private AABB cutoutBoundingBox = NULL_BOX;

    public static final EntityDataAccessor<Optional<UUID>> LINKED_PORTAL_UUID = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Boolean> IS_ACTIVE = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> STORED_OUTLINE = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.INT);
    /**
     * getAxisW() and getAxisH() define the orientation of the portal
     * They should be normalized and should be perpendicular to each other
     */
    public static final EntityDataAccessor<Optional<Vec3>> AXIS_W = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final EntityDataAccessor<Optional<Vec3>> AXIS_H = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final EntityDataAccessor<Vec3> AXIS_OH = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.VEC3D);
    public static final EntityDataAccessor<Vec3> AXIS_OW = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.VEC3D);
    public static final EntityDataAccessor<Optional<Vec3>> DESTINATION = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final EntityDataAccessor<Vec3> FACING = SynchedEntityData.defineId(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.VEC3D);
    public static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(ExperimentalPortal.class, EntityDataSerializers.OPTIONAL_UUID);

    private boolean disableValidation;

    public Vec3 getNormal() {
        return (getAxisW().orElseThrow(NOT_INIT).cross(getAxisH().orElseThrow(NOT_INIT)));
    }

    public Vec3 getOtherNormal() {
        return (getOtherAxisW().cross(getOtherAxisH()));
    }

    public ExperimentalPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(LINKED_PORTAL_UUID, Optional.empty());
        this.getEntityData().define(STORED_OUTLINE, "null");
        this.getEntityData().define(IS_ACTIVE, false);
        this.getEntityData().define(ROLL, 0f);
        this.getEntityData().define(COLOR, 0);
        this.getEntityData().define(AXIS_W, Optional.empty());
        this.getEntityData().define(AXIS_H, Optional.empty());
        this.getEntityData().define(AXIS_OH, Vec3.ZERO);
        this.getEntityData().define(AXIS_OW, Vec3.ZERO);
        this.getEntityData().define(DESTINATION, Optional.empty());
        this.getEntityData().define(FACING, Vec3.ZERO);
        this.getEntityData().define(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.setColor(nbt.getInt("color"));
        this.setRoll(nbt.getFloat("roll"));
        if (nbt.hasUUID("linkedPortalUUID")) this.setLinkedPortalUUID(Optional.of(nbt.getUUID("linkedPortalUUID")));
        if (nbt.contains("axisW")) this.setOrientation(IPHelperDuplicate.getVec3d(nbt, "axisW"), IPHelperDuplicate.getVec3d(nbt, "axisH"));
        this.setOtherAxisH(IPHelperDuplicate.getVec3d(nbt, "axisOH"));
        this.setOtherAxisW(IPHelperDuplicate.getVec3d(nbt, "axisOW"));
        if (nbt.contains("destination")) this.setDestination(Optional.of(IPHelperDuplicate.getVec3d(nbt, "destination")));
        this.setOtherFacing(IPHelperDuplicate.getVec3d(nbt, "facing"));
        if (nbt.hasUUID("ownerUUID")) this.setOwnerUUID(Optional.of(nbt.getUUID("ownerUUID")));
        disableValidation = nbt.getBoolean("DisableValidation");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("color", this.getColor());
        nbt.putFloat("roll", this.getRoll());
        this.getLinkedPortalUUID().ifPresent(uuid -> nbt.putUUID("linkedPortalUUID", uuid));
        this.getAxisW().ifPresent(axisW -> IPHelperDuplicate.putVec3d(nbt, "axisW", axisW));
        this.getAxisH().ifPresent(axisH -> IPHelperDuplicate.putVec3d(nbt, "axisH", axisH));
        IPHelperDuplicate.putVec3d(nbt, "axisOH", this.getOtherAxisH());
        IPHelperDuplicate.putVec3d(nbt, "axisOW", this.getOtherAxisW());
        this.getDestination().ifPresent(destination -> IPHelperDuplicate.putVec3d(nbt, "destination", destination));
        IPHelperDuplicate.putVec3d(nbt, "facing", this.getOtherFacing());
        this.getOwnerUUID().ifPresent(uuid -> nbt.putUUID("ownerUUID", uuid));
        nbt.putBoolean("DisableValidation", disableValidation);
    }

    public float getRoll() {
        return getEntityData().get(ROLL);
    }

    public void setRoll(float roll) {
        this.getEntityData().set(ROLL, roll);
    }

    public int getColor() {
        return getEntityData().get(COLOR);
    }

    public void setColor(int color) {
        this.getEntityData().set(COLOR, color);
    }

    @NotNull
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
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

    public Direction getFacingDirection() {
        return Direction.fromNormal((int) this.getNormal().x(), (int) this.getNormal().y(), (int) this.getNormal().z());
    }

    public Optional<Vec3> getAxisW() {
        return getEntityData().get(AXIS_W);
    }

    public Optional<Vec3> getAxisH() {
        return getEntityData().get(AXIS_H);
    }

    public Vec3 getOtherAxisH() {
        return getEntityData().get(AXIS_OH);
    }

    public Vec3 getOtherAxisW() {
        return getEntityData().get(AXIS_OW);
    }

    public void setOtherAxisH(Vec3 h) {
        this.getEntityData().set(AXIS_OH, h);
    }

    public void setOtherAxisW(Vec3 w) {
        this.getEntityData().set(AXIS_OW, w);
    }

    public Optional<Vec3> getDestination() {
        return getEntityData().get(DESTINATION);
    }

    public void setDestination(Optional<Vec3> destination) {
        this.getEntityData().set(DESTINATION, destination);
    }

    public Vec3 getOtherFacing() {
        return getEntityData().get(FACING);
    }

    public void setOtherFacing(Vec3 facing) {
        this.getEntityData().set(FACING, facing);
    }

    public void setOrientation(Vec3 axisW, Vec3 axisH) {
        this.getEntityData().set(AXIS_W, Optional.of(axisW));
        this.getEntityData().set(AXIS_H, Optional.of(axisH));
        syncRotations();
    }

    @Override
    public void kill() {
        getOwnerUUID().ifPresent(uuid -> {
            Entity player = ((ServerLevel) level).getEntity(uuid);
            CalledValues.removePortals(player, this.getUUID());
        });
        super.kill();
    }

    @Override
    public void tick() {
        if (getAxisH().isEmpty()) {
            PortalCubed.LOGGER.warn("Invalid portal {} found: axisH is absent. Removing.", this);
            discard();
            return;
        }
        this.makeBoundingBox();
        this.calculateCuttoutBox();
        if (!this.level.isClientSide)
            ((ServerLevel)(this.level)).setChunkForced(chunkPosition().x, chunkPosition().z, true);

        if (!level.isClientSide) {
            getOwnerUUID().ifPresent(uuid -> {
                Entity player = ((ServerLevel) level).getEntity(uuid);
                if (player == null || !player.isAlive()) {
                    this.kill();
                }
            });
        }

        if (!this.level.isClientSide && getAxisW().isPresent()) {
            ExperimentalPortal otherPortal =
                this.getLinkedPortalUUID().isPresent()
                    ? (ExperimentalPortal)((Accessors) level).getEntity(this.getLinkedPortalUUID().get())
                    : null;

            setActive(otherPortal != null);
            setDestination(Optional.of(Objects.requireNonNullElse(otherPortal, this).getOriginPos()));

            if (!validate()) {
                this.kill();
                level.playSound(null, this.position().x(), this.position().y(), this.position().z(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundSource.NEUTRAL, .1F, 1F);
            }
        }
        super.tick();
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
        final Direction forward = Direction.fromNormal(new BlockPos(getNormal()));
        assert forward != null;
        while (iter.advance()) {
            final BlockPos pos = new BlockPos(iter.nextX(), iter.nextY(), iter.nextZ());
            if (!AABB.of(BoundingBox.fromCorners(pos, pos)).intersects(portalBox)) continue;
            final BlockState state = level.getBlockState(pos);
            if (state.is(PortalCubedBlocks.PORTAL_NONSOLID) || state.is(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)) {
                final BlockState gelState = level.getBlockState(pos.relative(forward));
                final BooleanProperty property = MultifaceBlock.getFaceProperty(forward.getOpposite());
                if (!gelState.is(PortalCubedBlocks.PORTALABLE_GELS) || !gelState.getOptionalValue(property).orElse(false)) {
                    return false;
                }
            }
            final VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.of(this));
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
            final BlockState state = level.getBlockState(pos);
            if (state.is(PortalCubedBlocks.PORTAL_NONSOLID)) continue;
            if (state.is(PortalCubedBlocks.PORTAL_SOLID)) {
                return false;
            }
            final VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.of(this));
            if (
                shape.move(pos.getX(), pos.getY(), pos.getZ())
                    .toAabbs()
                    .stream()
                    .anyMatch(portalBox::intersects)
            ) return false;
        }
        return true;
    }

    public void syncRotations() {
        this.setBoundingBox(NULL_BOX);
        this.setCutoutBoundingBox(NULL_BOX);
        this.makeBoundingBox();
        this.calculateCuttoutBox();
        this.calculateBoundsCheckBox();
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        if (getAxisW().isEmpty()) {
            // it may be called when the portal is not yet initialized
            setBoundingBox(NULL_BOX);
            return NULL_BOX;
        }

        AABB portalBox = new AABB(
                getPointInPlane(width() / 2, height() / 2)
                        .add(getNormal().scale(.05)),
                getPointInPlane(-width() / 2, -height() / 2)
                        .add(getNormal().scale(-.2))
        ).minmax(new AABB(
                getPointInPlane(-width() / 2, height() / 2)
                        .add(getNormal().scale(.05)),
                getPointInPlane(width() / 2, -height() / 2)
                        .add(getNormal().scale(-.2))
        ));
        setBoundingBox(portalBox);
        return portalBox;
    }


    public AABB calculateCuttoutBox() {
        if (getAxisW().isEmpty()) {
            setCutoutBoundingBox(NULL_BOX);
            return NULL_BOX;
        }

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
        if (getAxisW().isEmpty()) {
            return NULL_BOX;
        }

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
        return getAxisW().orElseThrow(NOT_INIT).scale(xInPlane).add(getAxisH().orElseThrow(NOT_INIT).scale(yInPlane));
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

}
