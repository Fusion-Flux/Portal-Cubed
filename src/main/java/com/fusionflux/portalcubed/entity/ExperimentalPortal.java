package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPHelperDuplicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class  ExperimentalPortal extends Entity {

    private static final Box NULL_BOX = new Box(0, 0, 0, 0, 0, 0);

    private static final double WIDTH = 0.9, HEIGHT = 1.9;
    private static final double EPSILON = 1.0E-7;

    private Box cutoutBoundingBox = NULL_BOX;

    public static final TrackedData<Optional<UUID>> LINKED_PORTAL_UUID = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Boolean> IS_ACTIVE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STORED_OUTLINE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Float> ROLL = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> COLOR = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.INTEGER);
    /**
     * getAxisW() and getAxisH() define the orientation of the portal
     * They should be normalized and should be perpendicular to each other
     */
    public static final TrackedData<Optional<Vec3d>> AXIS_W = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final TrackedData<Optional<Vec3d>> AXIS_H = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final TrackedData<Vec3d> AXIS_OH = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.VEC3D);
    public static final TrackedData<Optional<Vec3d>> DESTINATION = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC3D);
    public static final TrackedData<Vec3d> FACING = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.VEC3D);
    public static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public Vec3d getNormal() {
        return getAxisW().get().crossProduct(getAxisH().get()).normalize();
    }

    public ExperimentalPortal(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LINKED_PORTAL_UUID, Optional.empty());
        this.getDataTracker().startTracking(STORED_OUTLINE, "null");
        this.getDataTracker().startTracking(IS_ACTIVE, false);
        this.getDataTracker().startTracking(ROLL, 0f);
        this.getDataTracker().startTracking(COLOR, 0);
        this.getDataTracker().startTracking(AXIS_W, Optional.empty());
        this.getDataTracker().startTracking(AXIS_H, Optional.empty());
        this.getDataTracker().startTracking(AXIS_OH, Vec3d.ZERO);
        this.getDataTracker().startTracking(DESTINATION, Optional.empty());
        this.getDataTracker().startTracking(FACING, Vec3d.ZERO);
        this.getDataTracker().startTracking(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        this.setColor(compoundTag.getInt("color"));
        this.setRoll(compoundTag.getFloat("roll"));
        if (compoundTag.containsUuid("linkedPortalUUID")) this.setLinkedPortalUUID(Optional.of(compoundTag.getUuid("linkedPortalUUID")));
        if (compoundTag.contains("axisW")) this.setOrientation(IPHelperDuplicate.getVec3d(compoundTag, "axisW").normalize(), IPHelperDuplicate.getVec3d(compoundTag, "axisH").normalize());
        this.setOtherAxisH(IPHelperDuplicate.getVec3d(compoundTag, "axisOH").normalize());
        if (compoundTag.contains("destination")) this.setDestination(Optional.of(IPHelperDuplicate.getVec3d(compoundTag, "destination")));
        this.setOtherFacing(IPHelperDuplicate.getVec3d(compoundTag, "facing"));
        if (compoundTag.containsUuid("ownerUUID")) this.setOwnerUUID(Optional.of(compoundTag.getUuid("ownerUUID")));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        compoundTag.putFloat("color", this.getColor());
        compoundTag.putFloat("roll", this.getRoll());
        this.getLinkedPortalUUID().ifPresent(uuid -> compoundTag.putUuid("linkedPortalUUID", uuid));
        this.getAxisW().ifPresent(axisW -> IPHelperDuplicate.putVec3d(compoundTag, "axisW", axisW));
        this.getAxisH().ifPresent(axisH -> IPHelperDuplicate.putVec3d(compoundTag, "axisH", axisH));
        IPHelperDuplicate.putVec3d(compoundTag, "axisOH", this.getOtherAxisH());
        this.getDestination().ifPresent(destination -> IPHelperDuplicate.putVec3d(compoundTag, "destination", destination));
        IPHelperDuplicate.putVec3d(compoundTag, "facing", this.getOtherFacing());
        this.getOwnerUUID().ifPresent(uuid -> compoundTag.putUuid("ownerUUID", uuid));
    }

    public float getRoll() {
        return getDataTracker().get(ROLL);
    }

    public void setRoll(float roll) {
        this.getDataTracker().set(ROLL, roll);
    }

    public int getColor() {
        return getDataTracker().get(COLOR);
    }

    public void setColor(int color) {
        this.getDataTracker().set(COLOR, color);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public Optional<UUID> getLinkedPortalUUID() {
        return getDataTracker().get(LINKED_PORTAL_UUID);
    }

    public void setLinkedPortalUUID(Optional<UUID> uuid) {
        this.getDataTracker().set(LINKED_PORTAL_UUID, uuid);
    }

    public Optional<UUID> getOwnerUUID() {
        return getDataTracker().get(OWNER_UUID);
    }

    public void setOwnerUUID(Optional<UUID> uuid) {
        this.getDataTracker().set(OWNER_UUID, uuid);
    }

    public boolean getActive() {
        return getDataTracker().get(IS_ACTIVE);
    }

    private void setActive(boolean active) {
        this.getDataTracker().set(IS_ACTIVE, active);
    }

    public Direction getFacingDirection() {
        return Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
    }

    public Optional<Vec3d> getAxisW() {
        return getDataTracker().get(AXIS_W);
    }

    public Optional<Vec3d> getAxisH() {
        return getDataTracker().get(AXIS_H);
    }

    public Vec3d getOtherAxisH() {
        return getDataTracker().get(AXIS_OH);
    }

    public void setOtherAxisH(Vec3d h) {
        this.getDataTracker().set(AXIS_OH, h);
    }

    public Optional<Vec3d> getDestination() {
        return getDataTracker().get(DESTINATION);
    }

    public void setDestination(Optional<Vec3d> destination) {
        this.getDataTracker().set(DESTINATION, destination);
    }

    public Vec3d getOtherFacing() {
        return getDataTracker().get(FACING);
    }

    public void setOtherFacing(Vec3d facing) {
        this.getDataTracker().set(FACING, facing);
    }

    public void setOrientation(Vec3d axisW, Vec3d axisH) {
        this.getDataTracker().set(AXIS_W, Optional.of(axisW));
        this.getDataTracker().set(AXIS_H, Optional.of(axisH));
        syncRotations();
    }

    @Override
    public void kill() {
        getOwnerUUID().ifPresent(uuid -> {
            Entity player = ((ServerWorld) world).getEntity(uuid);
            CalledValues.removePortals(player, this.getUuid());
        });
        super.kill();
    }

    @Override
    public void tick() {
        this.calculateBoundingBox();
        this.calculateCuttoutBox();
        this.calculateBoundsCheckBox();
        if (!this.world.isClient)
            ((ServerWorld)(this.world)).setChunkForced(getChunkPos().x, getChunkPos().z, true);

        if (!world.isClient) {
            getOwnerUUID().ifPresent(uuid -> {
                Entity player = ((ServerWorld) world).getEntity(uuid);
                if (player == null || !player.isAlive()) {
                    this.kill();
                }
            });
        }

        if (!this.world.isClient && getAxisW().isPresent()) {
            ExperimentalPortal otherPortal =
                this.getLinkedPortalUUID().isPresent()
                    ? (ExperimentalPortal)((Accessors) world).getEntity(this.getLinkedPortalUUID().get())
                    : null;

            setActive(otherPortal != null);
            setDestination(Optional.of(Objects.requireNonNullElse(otherPortal, this).getOriginPos()));

            if (!validate()) {
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
            }
        }
        super.tick();
    }

    public boolean validate() {
        return validateBehind() && validateFront();
    }

    private boolean validateBehind() {
        final Box portalBox = new Box(
            getPointInPlane(WIDTH / 2, HEIGHT / 2)
                .add(getNormal().multiply(-0.01)),
            getPointInPlane(-WIDTH / 2, -HEIGHT / 2)
                .add(getNormal().multiply(-0.2))
        ).union(new Box(
            getPointInPlane(-WIDTH / 2, HEIGHT / 2)
                .add(getNormal().multiply(-0.01)),
            getPointInPlane(WIDTH / 2, -HEIGHT / 2)
                .add(getNormal().multiply(-0.2))
        ));
        final CuboidBlockIterator iter = new CuboidBlockIterator(
            MathHelper.floor(portalBox.minX - EPSILON) - 1,
            MathHelper.floor(portalBox.minY - EPSILON) - 1,
            MathHelper.floor(portalBox.minZ - EPSILON) - 1,
            MathHelper.floor(portalBox.maxX + EPSILON) + 1,
            MathHelper.floor(portalBox.maxY + EPSILON) + 1,
            MathHelper.floor(portalBox.maxZ + EPSILON) + 1
        );
        while (iter.step()) {
            final BlockPos pos = new BlockPos(iter.getX(), iter.getY(), iter.getZ());
            if (!Box.from(BlockBox.create(pos, pos)).intersects(portalBox)) continue;
            final BlockState state = world.getBlockState(pos);
            if (state.isIn(PortalCubedBlocks.PORTAL_NONSOLID) || state.isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)) {
                return false;
            }
            final VoxelShape shape = state.getCollisionShape(world, pos, ShapeContext.of(this));
            if (
                shape.offset(pos.getX(), pos.getY(), pos.getZ())
                    .getBoundingBoxes()
                    .stream()
                    .noneMatch(portalBox::intersects)
            ) return false;
        }
        return true;
    }

    private boolean validateFront() {
        final Box portalBox = new Box(
            getPointInPlane(WIDTH / 2, HEIGHT / 2)
                .add(getNormal().multiply(0.2)),
            getPointInPlane(-WIDTH / 2, -HEIGHT / 2)
        ).union(new Box(
            getPointInPlane(-WIDTH / 2, HEIGHT / 2)
                .add(getNormal().multiply(0.2)),
            getPointInPlane(WIDTH / 2, -HEIGHT / 2)
        ));
        final CuboidBlockIterator iter = new CuboidBlockIterator(
            MathHelper.floor(portalBox.minX - EPSILON) - 1,
            MathHelper.floor(portalBox.minY - EPSILON) - 1,
            MathHelper.floor(portalBox.minZ - EPSILON) - 1,
            MathHelper.floor(portalBox.maxX + EPSILON) + 1,
            MathHelper.floor(portalBox.maxY + EPSILON) + 1,
            MathHelper.floor(portalBox.maxZ + EPSILON) + 1
        );
        while (iter.step()) {
            final BlockPos pos = new BlockPos(iter.getX(), iter.getY(), iter.getZ());
            if (!Box.from(BlockBox.create(pos, pos)).intersects(portalBox)) continue;
            final BlockState state = world.getBlockState(pos);
            if (state.isIn(PortalCubedBlocks.PORTAL_NONSOLID)) continue;
            if (state.isIn(PortalCubedBlocks.PORTAL_SOLID)) {
                return false;
            }
            final VoxelShape shape = state.getCollisionShape(world, pos, ShapeContext.of(this));
            if (
                shape.offset(pos.getX(), pos.getY(), pos.getZ())
                    .getBoundingBoxes()
                    .stream()
                    .anyMatch(portalBox::intersects)
            ) return false;
        }
        return true;
    }

    public static boolean allowedPortalBlock(World world, BlockPos pos) {
        final BlockState state = world.getBlockState(pos);
        return state.isIn(PortalCubedBlocks.PORTAL_NONSOLID) || state.getCollisionShape(world, pos).isEmpty();
    }

    public void syncRotations() {
        this.setBoundingBox(NULL_BOX);
        this.setCutoutBoundingBox(NULL_BOX);
        this.calculateBoundingBox();
        this.calculateCuttoutBox();
        this.calculateBoundsCheckBox();
    }

    @Override
    protected Box calculateBoundingBox() {
        if (getAxisW().isEmpty()) {
            // it may be called when the portal is not yet initialized
            setBoundingBox(NULL_BOX);
            return NULL_BOX;
        }

        Box portalBox = new Box(
                getPointInPlane(WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(.2)),
                getPointInPlane(-WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-.2))
        ).union(new Box(
                getPointInPlane(-WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(.2)),
                getPointInPlane(WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-.2))
        ));
        setBoundingBox(portalBox);
        return portalBox;
    }


    public Box calculateCuttoutBox() {
        if (getAxisW().isEmpty()) {
            setCutoutBoundingBox(NULL_BOX);
            return NULL_BOX;
        }

        Box portalBox = new Box(
                getCutoutPointInPlane(WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(5)),
                getCutoutPointInPlane(-WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-5))
        ).union(new Box(
                getCutoutPointInPlane(-WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(5)),
                getCutoutPointInPlane(WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-5))
        ));
        setCutoutBoundingBox(portalBox);
        return portalBox;
    }


    public Box calculateBoundsCheckBox() {
        if (getAxisW().isEmpty()) {
            return NULL_BOX;
        }

        return new Box(
                getBoundsCheckPointInPlane(WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(10)),
                getBoundsCheckPointInPlane(-WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-10))
        ).union(new Box(
                getBoundsCheckPointInPlane(-WIDTH / 2, HEIGHT / 2)
                        .add(getNormal().multiply(10)),
                getBoundsCheckPointInPlane(WIDTH / 2, -HEIGHT / 2)
                        .add(getNormal().multiply(-10))
        ));
    }

    public final Box getCutoutBoundingBox() {
        return this.cutoutBoundingBox;
    }

    public final void setCutoutBoundingBox(Box boundingBox) {
        this.cutoutBoundingBox = boundingBox;
    }

    public Vec3d getCutoutPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().getUnitVector().getX() * -5, getFacingDirection().getUnitVector().getY() * -5, getFacingDirection().getUnitVector().getZ() * -5);
    }

    public Vec3d getBoundsCheckPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane));
    }

    public Vec3d getPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane));
    }

    public Vec3d getPointInPlaneLocal(double xInPlane, double yInPlane) {
        return getAxisW().get().multiply(xInPlane).add(getAxisH().get().multiply(yInPlane));
    }

    public Vec3d getOriginPos() {
        return getPos();
    }

    public void setOriginPos(Vec3d pos) {
        setPosition(pos);
    }

}
