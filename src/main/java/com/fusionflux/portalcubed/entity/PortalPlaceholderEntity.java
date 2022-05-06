package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.QuaternionHandler;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;

import com.fusionflux.portalcubed.util.IPHelperDuplicate;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PortalPlaceholderEntity extends Entity {
    public static final TrackedData<Quaternion> QUATERNION = DataTracker.registerData(PortalPlaceholderEntity.class, QuaternionHandler.QUATERNION_HANDLER);
    public static final TrackedData<Float> ROLL = DataTracker.registerData(PortalPlaceholderEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> COLOR = DataTracker.registerData(PortalPlaceholderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public Vec3d axisH;
    public Vec3d axisW;
    public Quaternion rotation = Quaternion.IDENTITY;

    public PortalPlaceholderEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(QUATERNION, new Quaternion(0, 0, 0, 1));
        this.getDataTracker().startTracking(ROLL, 0f);
        this.getDataTracker().startTracking(COLOR, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        this.setColor(compoundTag.getInt("color"));
        this.setRoll(compoundTag.getFloat("roll"));
        this.axisH = IPHelperDuplicate.getVec3d(compoundTag, "axisH").normalize();
        this.axisW = IPHelperDuplicate.getVec3d(compoundTag, "axisW").normalize();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        compoundTag.putFloat("color", this.getColor());
        compoundTag.putFloat("roll", this.getRoll());
        IPHelperDuplicate.putVec3d(compoundTag, "axisH", this.axisH);
        IPHelperDuplicate.putVec3d(compoundTag, "axisW", this.axisW);
    }

    public Quaternion getRotation() {
        return getDataTracker().get(QUATERNION);
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
    public boolean canUsePortals() {
        return false;
    }


    @Override
    public boolean isCollidable() {
        return this.isAlive();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = 64.0D * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    public Direction getHorizontalFacing() {
        return Direction.fromRotation(this.getYaw());
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


}
