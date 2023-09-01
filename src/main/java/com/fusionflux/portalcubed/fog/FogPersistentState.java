package com.fusionflux.portalcubed.fog;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

public class FogPersistentState extends SavedData {
	@Nullable
	private FogSettings settings = null;

	public FogPersistentState() {
	}

	public FogPersistentState(@Nullable FogSettings settings) {
		this.settings = settings;
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		if (settings != null) {
			nbt.put("Settings", settings.writeNbt(new CompoundTag()));
		}
		return nbt;
	}

	public static FogPersistentState readNbt(CompoundTag nbt) {
		return new FogPersistentState(FogSettings.readNbt(nbt.getCompound("Settings")));
	}

	@Nullable
	public FogSettings getSettings() {
		return settings;
	}

	public void setSettings(@Nullable FogSettings settings) {
		this.settings = settings;
		setDirty();
	}

	public static FogPersistentState getOrCreate(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(
			FogPersistentState::readNbt, FogPersistentState::new, "pc_custom_fog"
		);
	}
}
