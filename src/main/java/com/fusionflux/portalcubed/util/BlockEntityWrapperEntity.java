package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException("Shouldn't call getAddEntityPacket() on BlockEntityWrapperEntity.");
    }

    public E getBlockEntity() {
        return blockEntity;
    }
}
