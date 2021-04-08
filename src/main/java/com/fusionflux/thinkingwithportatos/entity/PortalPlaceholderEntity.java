package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.accessor.QuaternionHandler;
import com.qouteall.immersive_portals.Helper;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class PortalPlaceholderEntity extends Entity {

    public static final Identifier SPAWN_PACKET = id("portal_placeholder_spawn");
    public Vec3d axisH;
    public Vec3d axisW;
    public static final TrackedData<Quaternion> QUATERNION = DataTracker.registerData(PortalPlaceholderEntity.class, QuaternionHandler.QUATERNION_HANDLER);
    public static final TrackedData<Float> ROLL = DataTracker.registerData(PortalPlaceholderEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private Vec3d normal;
    public static final TrackedData<Integer> COLOR = DataTracker.registerData(PortalPlaceholderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public Quaternion rotation = Quaternion.IDENTITY;
    public int color;
    //public int color;

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
    protected void readCustomDataFromTag(CompoundTag compoundTag) {
        this.setColor(compoundTag.getInt("color"));
        this.setRoll(compoundTag.getFloat("roll"));
        this.axisH = Helper.getVec3d(compoundTag, "axisH").normalize();
        this.axisW = Helper.getVec3d(compoundTag, "axisW").normalize();

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putFloat("color", this.getColor());
        compoundTag.putFloat("roll", this.getRoll());
        Helper.putVec3d(compoundTag, "axisH", this.axisH);
        Helper.putVec3d(compoundTag, "axisW", this.axisW);

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

    public Vec3d getNormal() {
        if (this.normal == null) {
            this.normal = this.axisW.crossProduct(this.axisH).normalize();
        }

        return this.normal;
    }


    public Integer getColor() {
        return getDataTracker().get(COLOR);
    }

    public void setColor(Integer color) {
        this.getDataTracker().set(COLOR, color);

    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = 64.0D * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return true;
    }

    @Override
    public Direction getHorizontalFacing() {
        return Direction.fromRotation(this.yaw);
    }


    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()))
                .writeUuid(this.getUuid())
                .writeVarInt(this.getEntityId())
                .writeDouble(this.getX())
                .writeDouble(this.getY())
                .writeDouble(this.getZ())
                .writeByte(MathHelper.floor(this.pitch * 256.0F / 360.0F))
                .writeByte(MathHelper.floor(this.yaw * 256.0F / 360.0F));

        return ServerPlayNetworking.createS2CPacket(SPAWN_PACKET, packet);
    }


}
