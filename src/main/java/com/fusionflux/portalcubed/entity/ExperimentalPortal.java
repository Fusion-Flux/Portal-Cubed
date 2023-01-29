package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Objects;
import java.util.UUID;

public class ExperimentalPortal extends Entity {

    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);

    private Box cutoutBoundingBox = nullBox;
    private Box intersectionBoundingBox = nullBox;
    /**
     * axisW and axisH define the orientation of the portal
     * They should be normalized and should be perpendicular to each other
     */

    public static final TrackedData<String> LINKED_PORTAL_UUID = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> ISACTIVE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STOREDOUTLINE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Float> ROLL = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> COLOR = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.INTEGER);


    public Vec3d getNormal() {
        return CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).normalize();
    }

    public Vec3d getNormalB() {
        return CalledValues.getAxisH(this).crossProduct(CalledValues.getAxisW(this)).normalize();
    }

    public ExperimentalPortal(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LINKED_PORTAL_UUID, "null");
        this.getDataTracker().startTracking(STOREDOUTLINE, "null");
        this.getDataTracker().startTracking(ISACTIVE, false);
        this.getDataTracker().startTracking(ROLL, 0f);
        this.getDataTracker().startTracking(COLOR, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        this.setColor(compoundTag.getInt("color"));
        this.setRoll(compoundTag.getFloat("roll"));
        this.setLinkedPortalUuid(StringUtils.defaultIfEmpty(compoundTag.getString("linkedPortalUuid"), "null"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        compoundTag.putFloat("color", this.getColor());
        compoundTag.putFloat("roll", this.getRoll());
        compoundTag.putString("linkedPortalUuid", this.getLinkedPortalUuid());
    }

    public Float getRoll() {
        return getDataTracker().get(ROLL);
    }

    public void setRoll(Float roll) {
        this.getDataTracker().set(ROLL, roll);
    }


    public Integer getColor() {
        return getDataTracker().get(COLOR);
    }

    public void setColor(Integer color) {
        this.getDataTracker().set(COLOR, color);

    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

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

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
return true;
    }


    public String getLinkedPortalUuid() {
        return getDataTracker().get(LINKED_PORTAL_UUID);
    }

    public void setLinkedPortalUuid(String string) {
        this.getDataTracker().set(LINKED_PORTAL_UUID, string);
    }

    public String getOutline() {
        return getDataTracker().get(STOREDOUTLINE);
    }

    public void setOutline(String outline) {
        this.getDataTracker().set(STOREDOUTLINE, outline);
    }

    public Boolean getActive() {
        return getDataTracker().get(ISACTIVE);
    }

    private void setActive(Boolean active) {
        this.getDataTracker().set(ISACTIVE, active);
    }

    public Direction getFacingDirection(){
        return Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
    }

    public Direction getFacingDirectionB(){
        return Direction.fromVector((int) this.getNormalB().getX(), (int) this.getNormalB().getY(), (int) this.getNormalB().getZ());
    }

    @Override
    public void kill() {
        Entity player = ((ServerWorld) world).getEntity(CalledValues.getPlayer(this));
        if(CalledValues.getPortals(player).contains(this.getUuid())) {
            CalledValues.removePortals(player, this.getUuid());
        }
        super.kill();
    }

    @Override
    public void tick() {
        //if(this.getBoundingBox() == nullBox){
            this.calculateBoundingBox();
        //}
        //if(this.getCutoutBoundingBox() == nullBox){
            this.calculateCuttoutBox();
        //}
        if(!this.world.isClient)
            ((ServerWorld)(this.world)).setChunkForced(getChunkPos().x,getChunkPos().z,true);
        //if(this.getIntersectionBoundingBox() == nullBox){
        //    this.calculateIntersectionBox();
        //}

        if(!world.isClient){
            final UUID playerUuid = CalledValues.getPlayer(this);
            if (playerUuid != null) {
                Entity player = ((ServerWorld) world).getEntity(playerUuid);
                if(player == null || !player.isAlive()){
                    this.kill();
                }
            }
        }

        if (!this.world.isClient && CalledValues.getAxisW(this) != null) {
            ExperimentalPortal otherPortal =
                !this.getLinkedPortalUuid().equals("null")
                    ? (ExperimentalPortal)((ServerWorld)world).getEntity(UUID.fromString(this.getLinkedPortalUuid()))
                    : null;

            setActive(otherPortal != null);
            CalledValues.setDestination(this, Objects.requireNonNullElse(otherPortal, this).getOriginPos());

            BlockPos topBehind = new BlockPos(
                    this.getPos().getX() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getX(),
                    this.getPos().getY() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getY(),
                    this.getPos().getZ() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getZ());
            BlockPos bottomBehind = new BlockPos(
                    this.getPos().getX() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getX() - Math.abs(CalledValues.getAxisH(this).getX()),
                    this.getPos().getY() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getY() + CalledValues.getAxisH(this).getY(),
                    this.getPos().getZ() - CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).getZ() - Math.abs(CalledValues.getAxisH(this).getZ()));
            BlockPos bottom = new BlockPos(
                    this.getPos().getX() - Math.abs(CalledValues.getAxisH(this).getX()),
                    this.getPos().getY() + CalledValues.getAxisH(this).getY(),
                    this.getPos().getZ() - Math.abs(CalledValues.getAxisH(this).getZ()));


            Direction portalFacing = Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
            boolean topValidBlock=false;
            if(this.world.getBlockState(this.getBlockPos()).isIn(PortalCubedBlocks.GELCHECKTAG)&&this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());

                topValidBlock = this.world.getBlockState(this.getBlockPos()).get(booleanProperty);
            }else if (!this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                topValidBlock=true;
            }
            boolean bottomValidBlock=false;
            if(this.world.getBlockState(bottom).isIn(PortalCubedBlocks.GELCHECKTAG)&&this.world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
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
        //this.setIntersectionBoundingBox(nullBox);
        this.calculateBoundingBox();
        this.calculateCuttoutBox();
        //this.calculateIntersectionBox();
    }

    @Override
    protected Box calculateBoundingBox() {
        if (CalledValues.getAxisW(this) == null) {
            // it may be called when the portal is not yet initialized
            setBoundingBox(nullBox);
            return nullBox;
        }
            double w =.9;
            double h = 1.9;


            //setBoundingBox(nullBox);
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
        if (CalledValues.getAxisW(this) == null) {
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

    public Box calculateIntersectionBox() {
        if (CalledValues.getAxisW(this) == null) {
            setIntersectionBoundingBox(nullBox);
            return nullBox;
        }
        double w = .9;
        double h = 1.9;
        Box portalBox = new Box(
                getIntersectionPointInPlane(w / 2, h / 2)
                        .add(getNormal().multiply(.2)),
                getIntersectionPointInPlane(-w / 2, -h / 2)
                        .add(getNormal().multiply(-.2))
        ).union(new Box(
                getIntersectionPointInPlane(-w / 2, h / 2)
                        .add(getNormal().multiply(.2)),
                getIntersectionPointInPlane(w / 2, -h / 2)
                        .add(getNormal().multiply(-.2))
        ));
        setIntersectionBoundingBox(portalBox.offset(getFacingDirection().getOffsetX()*.3,getFacingDirection().getOffsetY()*.3,getFacingDirection().getOffsetZ()*.3));
        return portalBox;
    }

    public final Box getCutoutBoundingBox() {
        return this.cutoutBoundingBox;
    }

    public final Box getIntersectionBoundingBox(double width) {
        return this.getBoundingBox().offset(getFacingDirection().getOffsetX()*width,getFacingDirection().getOffsetY()*width,getFacingDirection().getOffsetZ()*width);
    }

    public final void setCutoutBoundingBox(Box boundingBox) {
        this.cutoutBoundingBox = boundingBox;
    }

    public final void setIntersectionBoundingBox(Box boundingBox) {
        this.intersectionBoundingBox = boundingBox;
    }


    public Vec3d getCutoutPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().getUnitVector().getX()*-5,getFacingDirection().getUnitVector().getY()*-5,getFacingDirection().getUnitVector().getZ()*-5);
    }

    public Vec3d getIntersectionPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane)).add(getFacingDirection().getUnitVector().getX()*5,getFacingDirection().getUnitVector().getY()*5,getFacingDirection().getUnitVector().getZ()*5);
    }

    public Vec3d getPointInPlane(double xInPlane, double yInPlane) {
        return getOriginPos().add(getPointInPlaneLocal(xInPlane, yInPlane));
    }

    public Vec3d getPointInPlaneLocal(double xInPlane, double yInPlane) {
        return CalledValues.getAxisW(this).multiply(xInPlane).add(CalledValues.getAxisH(this).multiply(yInPlane));
    }

    public Vec3d getOriginPos() {
        return getPos();
    }

    public void setOriginPos(Vec3d pos) {
        setPosition(pos);
    }

}
