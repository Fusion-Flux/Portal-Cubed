package com.fusionflux.portalcubed.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;

public final class BlockEntityWrapperEntity<E extends BlockEntity> extends Entity {
    private final E blockEntity;

    public BlockEntityWrapperEntity(E blockEntity) {
        super(EntityType.PIG, blockEntity.getWorld());
        this.blockEntity = blockEntity;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }

    public E getBlockEntity() {
        return blockEntity;
    }
}
