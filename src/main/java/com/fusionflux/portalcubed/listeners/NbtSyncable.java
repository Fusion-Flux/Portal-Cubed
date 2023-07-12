package com.fusionflux.portalcubed.listeners;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface NbtSyncable {
    void syncNbt(CompoundTag nbt, ServerPlayer player) throws CommandRuntimeException;
}
