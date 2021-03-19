package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.client.render.model.entity.PortalPlaceholderModel;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class PortalPlaceholderEntity extends Entity {

    public static final Identifier SPAWN_PACKET = id("portal_placeholder_spawn");

    public Quaternion rotation;

    public int color;

    public PortalPlaceholderEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag compoundTag) {
        //this.color = compoundTag.getDouble("color");
        CompoundTag rotation = compoundTag.getCompound("rotation"); // "tag" is the argument in the method
        float w = rotation.getFloat("w");
        float x = rotation.getFloat("x");
        float y = rotation.getFloat("y");
        float z = rotation.getFloat("z");
        this.rotation = new Quaternion(x,y,z,w);
       // this.rotation = new Quaternion(compoundTag.getFloat("x"),compoundTag.getFloat("y"),compoundTag.getFloat("z"),compoundTag.getFloat("w"));
        this.color = compoundTag.getInt("color");
    }

    public void setRotationTransformation(Quaternion quaternion) {
        this.rotation = quaternion;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compoundTag) {
        if (this.rotation != null) {
            CompoundTag rotation = new CompoundTag();
            rotation.putFloat("x", this.rotation.getX());
            rotation.putFloat("y", this.rotation.getY());
            rotation.putFloat("z", this.rotation.getZ());
            rotation.putFloat("w", this.rotation.getW());

            compoundTag.put("rotation", rotation); // "tag" is the argument in the method
    }
        compoundTag.putInt("color", this.color);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = 1;
        if (Double.isNaN(d)) {
            d = 1.0D;
        }

        d *= 64.0D;
        return distance < d * d;
    }

@Override
    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return true;
    }

@Override
    public Direction getHorizontalFacing() {
        return Direction.fromRotation((double)this.yaw);
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
