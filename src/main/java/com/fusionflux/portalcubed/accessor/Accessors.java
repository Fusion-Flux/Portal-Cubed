package com.fusionflux.portalcubed.accessor;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Accessors {

    @Nullable
    Entity getEntity(UUID uuid);

}
