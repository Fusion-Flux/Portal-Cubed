package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPHelperDuplicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class ExperimentalPortal extends Entity {

    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);

    private Box cutoutBoundingBox = nullBox;

    private Vec3d axisOH = Vec3d.ZERO;
    private Vec3d destination = null;
    private Vec3d facing = Vec3d.ZERO;
    private Optional<UUID> ownerUUID = Optional.empty();

    public static final TrackedData<Optional<UUID>> LINKED_PORTAL_UUID = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Boolean> IS_ACTIVE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STORED_OUTLINE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Float> ROLL = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> COLOR = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.INTEGER);
    /**
     * getAxisW() and getAxisH() define the orientation of the portal
     * They should be normalized and should be perpendicular to each other
     */
    public static final TrackedData<Optional<Vec3d>> AXIS_W = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC_3D);
    public static final TrackedData<Optional<Vec3d>> AXIS_H = DataTracker.registerData(ExperimentalPortal.class, PortalCubedTrackedDataHandlers.OPTIONAL_VEC_3D);

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
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        this.setColor(compoundTag.getInt("color"));
        this.setRoll(compoundTag.getFloat("roll"));
        if (compoundTag.containsUuid("linkedPortalUUID")) this.setLinkedPortalUUID(Optional.of(compoundTag.getUuid("linkedPortalUUID")));
        if (compoundTag.contains("axisW")) this.setOrientation(IPHelperDuplicate.getVec3d(compoundTag, "axisW").normalize(), IPHelperDuplicate.getVec3d(compoundTag, "axisH").normalize());
        this.setOtherAxisH(IPHelperDuplicate.getVec3d(compoundTag, "axisOH").normalize());
        this.setDestination(IPHelperDuplicate.getVec3d(compoundTag, "destination"));
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
        IPHelperDuplicate.putVec3d(compoundTag, "destination", this.getDestination());
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
        final var packet = PacketByteBufs.create();

        packet.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()))
                .writeUuid(this.getUuid())
                .writeVarInt(this.getId())
                .writeDouble(this.getX())
                .writeDouble(this.getY())
                .writeDouble(this.getZ())
                .writeByte(MathHelper.floor(this.getPitch() * 256.0F / 360.0F))
                .writeByte(MathHelper.floor(this.getYaw() * 256.0F / 360.0F));
        return ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.SPAWN_PACKET, packet);
    }

    public Optional<UUID> getLinkedPortalUUID() {
        return getDataTracker().get(LINKED_PORTAL_UUID);
    }

    public void setLinkedPortalUUID(Optional<UUID> uuid) {
        this.getDataTracker().set(LINKED_PORTAL_UUID, uuid);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(Optional<UUID> uuid) {
        this.ownerUUID = uuid;
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
        return axisOH;
    }

    public void setOtherAxisH(Vec3d h) {
        axisOH = h;
    }

    public Vec3d getDestination() {
        return destination;
    }

    public void setDestination(Vec3d Destination) {
        destination = Destination;
    }

    public Vec3d getOtherFacing() {
        return facing;
    }

    public void setOtherFacing(Vec3d Facing) {
        facing = Facing;
    }

    public void setOrientation(Vec3d axisW, Vec3d axisH) {
        this.getDataTracker().set(AXIS_W, Optional.of(axisW));
        this.getDataTracker().set(AXIS_H, Optional.of(axisH));
        syncRotations();
    }

    @Override
    public void kill() {
        ownerUUID.ifPresent(uuid -> {
            Entity player = ((ServerWorld) world).getEntity(uuid);
            CalledValues.removePortals(player, this.getUuid());
        });
        super.kill();
    }

    @Override
    public void tick() {
            this.calculateBoundingBox();
            this.calculateCuttoutBox();
        if(!this.world.isClient)
            ((ServerWorld)(this.world)).setChunkForced(getChunkPos().x,getChunkPos().z,true);

        if(!world.isClient){
            ownerUUID.ifPresent(uuid -> {
                Entity player = ((ServerWorld) world).getEntity(uuid);
                if(player == null || !player.isAlive()){
                    this.kill();
                }
            });
        }

        if (!this.world.isClient && getAxisW().isPresent()) {
            ExperimentalPortal otherPortal = 
                this.getLinkedPortalUUID().isPresent()
                    ? (ExperimentalPortal)((ServerWorld)world).getEntity(this.getLinkedPortalUUID().get())
                    : null;

            setActive(otherPortal != null);
            this.destination = Objects.requireNonNullElse(otherPortal, this).getOriginPos();

            BlockPos topBehind = new BlockPos(
                    this.getPos().getX() - getAxisW().get().crossProduct(getAxisH().get()).getX(),
                    this.getPos().getY() - getAxisW().get().crossProduct(getAxisH().get()).getY(),
                    this.getPos().getZ() - getAxisW().get().crossProduct(getAxisH().get()).getZ());
            BlockPos bottomBehind = new BlockPos(
                    this.getPos().getX() - getAxisW().get().crossProduct(getAxisH().get()).getX() - Math.abs(getAxisH().get().getX()),
                    this.getPos().getY() - getAxisW().get().crossProduct(getAxisH().get()).getY() + getAxisH().get().getY(),
                    this.getPos().getZ() - getAxisW().get().crossProduct(getAxisH().get()).getZ() - Math.abs(getAxisH().get().getZ()));
            BlockPos bottom = new BlockPos(
                    this.getPos().getX() - Math.abs(getAxisH().get().getX()),
                    this.getPos().getY() + getAxisH().get().getY(),
                    this.getPos().getZ() - Math.abs(getAxisH().get().getZ()));


            Direction portalFacing = Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
            boolean topValidBlock=false;
            if(this.world.getBlockState(this.getBlockPos()).isIn(PortalCubedBlocks.GEL_CHECK_TAG)&&this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                assert portalFacing != null;
                BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());

                topValidBlock = this.world.getBlockState(this.getBlockPos()).get(booleanProperty);
            }else if (!this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                topValidBlock=true;
            }
            boolean bottomValidBlock=false;
            if(this.world.getBlockState(bottom).isIn(PortalCubedBlocks.GEL_CHECK_TAG)&&this.world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                assert portalFacing != null;
                BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());
                bottomValidBlock = this.world.getBlockState(bottom).get(booleanProperty);
            }else if (!this.world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                bottomValidBlock=true;
            }

            if ((!this.world.getBlockState(topBehind).isSideSolidFullSquare(world, topBehind, portalFacing)) ||
                    (!this.world.getBlockState(bottomBehind).isSideSolidFullSquare(world, bottomBehind, portalFacing) ||
                            !topValidBlock ||
                            !bottomValidBlock)||
                    ((!this.world.getBlockState(this.getBlockPos()).isAir())&& !this.world.getBlockState(this.getBlockPos()).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN) )|| (!this.world.getBlockState(bottom).isAir() && !this.world.getBlockState(bottom).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN))) {
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
            }
        }
        super.tick();
    }

    public void syncRotations(){
        this.setBoundingBox(nullBox);
        this.setCutoutBoundingBox(nullBox);
        this.calculateBoundingBox();
        this.calculateCuttoutBox();
    }

    @Override
    protected Box calculateBoundingBox() {
        if (getAxisW().isEmpty()) {
            // it may be called when the portal is not yet initialized
            setBoundingBox(nullBox);
            return nullBox;
        }
            double w =.9;
            double h = 1.9;


            Box portalBox = new Box(
                    getPointInPlane(w / 2, h / 2)
                            .add(getNormal().multiply(.2)),
                    getPointInPlane(-w / 2, -h / 2)
                            .add(getNormal().multiply(-.2))
            ).union(new Box(
                    getPointInPlane(-w / 2, h / 2)
                            .add(getNormal().multiply(.2)),
                    getPointInPlane(w / 2, -h / 2)
                            .add(getNormal().multiply(-.2))
            ));
            setBoundingBox(portalBox);
            return portalBox;
    }


    public Box calculateCuttoutBox() {
        if (getAxisW().isEmpty()) {
            setCutoutBoundingBox(nullBox);
            return nullBox;
        }
        double w = .9;
        double h = 1.9;
        Box portalBox = new Box(
                getCutoutPointInPlane(w / 2, h / 2)
                        .add(getNormal().multiply(5)),
                getCutoutPointInPlane(-w / 2, -h / 2)
                        .add(getNormal().multiply(-5))
        ).union(new Box(
                getCutoutPointInPlane(-w / 2, h / 2)
                        .add(getNormal().multiply(5)),
                getCutoutPointInPlane(w / 2, -h / 2)
                        .add(getNormal().multiply(-5))
        ));
        setCutoutBoundingBox(portalBox);
        return portalBox;
    }

    public final Box getCutoutBoundingBox() {
        return this.cutoutBoundingBox;
    }

    public final void setCutoutBoundingBox(Box boundingBox) {
        this.cutoutBoundingBox = boundingBox;
    }

    public Vec3d getCutoutPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().getUnitVector().getX()*-5,getFacingDirection().getUnitVector().getY()*-5,getFacingDirection().getUnitVector().getZ()*-5);
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
