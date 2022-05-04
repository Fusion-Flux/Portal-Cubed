package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.CustomTrackedData;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ExperimentalPortal extends Entity {

    public static final UUID nullUUID = Util.NIL_UUID;
    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);

    /**
     * axisW and axisH define the orientation of the portal
     * They should be normalized and should be perpendicular to each other
     */

    //public Vec3d destination;

    //private Vec3d normal;

    public static final TrackedData<String> STOREDSTRING = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> ISACTIVE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STOREDOUTLINE = DataTracker.registerData(ExperimentalPortal.class, TrackedDataHandlerRegistry.STRING);


    public Vec3d getNormal() {
        //if (normal == null) {
            return CalledValues.getAxisW(this).crossProduct(CalledValues.getAxisH(this)).normalize();
       // }
        //return normal;
    }

    public ExperimentalPortal(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
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

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STOREDSTRING, "null");
        this.getDataTracker().startTracking(STOREDOUTLINE, "null");
        this.getDataTracker().startTracking(ISACTIVE, false);
    }

    public String getString() {
        return getDataTracker().get(STOREDSTRING);
    }

    public void setString(String string) {
        this.getDataTracker().set(STOREDSTRING, string);
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

    public void setActive(Boolean active) {
        this.getDataTracker().set(ISACTIVE, active);
    }

    public Direction getFacingDirection(){
        return Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
    }

    boolean updateClient = false;

    @Override
    public void tick() {
        if(this.getBoundingBox() == nullBox){
            this.calculateBoundingBox();
        }
        if(CalledValues.getDestination(this) != null) {
            if (!this.world.isClient) {
                List<Entity> list = world.getEntitiesByClass(Entity.class, this.getBoundingBox(), e -> true);
                for (Entity entity : list) {
                    //System.out.println(entity);
                    if (!this.getString().equals("null")) {
                        ExperimentalPortal otherPortal;
                        otherPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                        assert otherPortal != null;


                        if (!(entity instanceof ExperimentalPortal)) {
                           // CalledValues.teleportEntity(this,CalledValues.getDestination(this),entity,otherPortal);
                          //  if (entity.horizontalCollision && this.getFacingDirection() != Direction.UP && this.getFacingDirection() != Direction.DOWN) {
                          //      entity.teleport(CalledValues.getDestination(this).getX(), CalledValues.getDestination(this).getY(), CalledValues.getDestination(this).getZ());
                          //  }
                          //  if (entity.verticalCollision && this.getFacingDirection() == Direction.UP && this.getFacingDirection() == Direction.DOWN) {
                          //      entity.teleport(CalledValues.getDestination(this).getX(), CalledValues.getDestination(this).getY(), CalledValues.getDestination(this).getZ());
                          //  }
                        }
                    }
                }
            }
        }

        if (!this.world.isClient && CalledValues.getAxisW(this) != null) {
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
                if (!this.getOutline().equals("null")) {
                    PortalPlaceholderEntity portalOutline;
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getOutline()));
                    assert portalOutline != null;
                    if (portalOutline != null) {
                        portalOutline.kill();
                    }
                }
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                if (!this.getString().equals("null")) {
                    ExperimentalPortal otherPortal;
                    otherPortal = (ExperimentalPortal) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    if (otherPortal != null) {
                        CalledValues.setDestination(otherPortal,otherPortal.getOriginPos());
                        //otherPortal.setDestination(otherPortal.getOriginPos());
                        //PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                        otherPortal.setActive(false);
                        //otherPortal.reloadAndSyncToClient();
                    }
                }
            }
        }
        super.tick();
    }

    public void syncRotations(){
        this.setBoundingBox(nullBox);
    }

    @Override
    protected Box calculateBoundingBox() {
        if (CalledValues.getAxisW(this) == null) {
            // it may be called when the portal is not yet initialized
            setBoundingBox(nullBox);
            return nullBox;
        }
            double w = .9;
            double h = 1.9;
            //setBoundingBox(nullBox);
            Box portalBox = new Box(
                    getPointInPlane(w / 2, h / 2)
                            .add(getNormal().multiply(0.2)),
                    getPointInPlane(-w / 2, -h / 2)
                            .add(getNormal().multiply(-0.2))
            ).union(new Box(
                    getPointInPlane(-w / 2, h / 2)
                            .add(getNormal().multiply(0.2)),
                    getPointInPlane(w / 2, -h / 2)
                            .add(getNormal().multiply(-0.2))
            ));
            setBoundingBox(portalBox);
            return portalBox;
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


    //public void setOrientationAndSize(
    //        Vec3d newAxisW, Vec3d newAxisH
    //) {
    //    setBoundingBox(nullBox);
    //    System.out.println("AAA");
    //    CalledValues.setAxisW(this,newAxisW);
    //    CalledValues.setAxisH(this,newAxisH);
    //    calculateBoundingBox();
    //}
}
