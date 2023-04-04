package com.fusionflux.portalcubed.fog;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

public class FogPersistentState extends PersistentState {
    @Nullable
    private FogSettings settings = null;

    public FogPersistentState() {
    }

    public FogPersistentState(@Nullable FogSettings settings) {
        this.settings = settings;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (settings != null) {
            nbt.put("Settings", settings.writeNbt(new NbtCompound()));
        }
        return nbt;
    }

    public static FogPersistentState readNbt(NbtCompound nbt) {
        return new FogPersistentState(FogSettings.readNbt(nbt.getCompound("Settings")));
    }

    @Nullable
    public FogSettings getSettings() {
        return settings;
    }

    public void setSettings(@Nullable FogSettings settings) {
        this.settings = settings;
        markDirty();
    }

    public static FogPersistentState getOrCreate(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            FogPersistentState::readNbt, FogPersistentState::new, "pc_custom_fog"
        );
    }
}
