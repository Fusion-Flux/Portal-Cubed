package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class EnergyPelletEntity extends Entity {
    private static final TrackedData<Float> SPEED = DataTracker.registerData(EnergyPelletEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public EnergyPelletEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(SPEED, 1f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        setSpeed(nbt.getFloat("Speed"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("Speed", getSpeed());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public float getSpeed() {
        return dataTracker.get(SPEED);
    }

    public void setSpeed(float speed) {
        dataTracker.set(SPEED, speed);
    }
}
