package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BlockEntityWrapperEntity<E extends BlockEntity> extends Entity {
    private final E blockEntity;

    public BlockEntityWrapperEntity(E blockEntity) {
        super(EntityType.PIG, blockEntity.getLevel());
        this.blockEntity = blockEntity;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    public E getBlockEntity() {
        return blockEntity;
    }
}
