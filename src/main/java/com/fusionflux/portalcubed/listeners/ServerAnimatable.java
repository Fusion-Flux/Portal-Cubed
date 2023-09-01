package com.fusionflux.portalcubed.listeners;

import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.Nullable;

public interface ServerAnimatable {
	int getAge();

	@Nullable
	AnimationState getAnimation(String name);
}
