package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface LevelExt {
    PortalCubedDamageSources pcDamageSources();

    @Nullable
    Entity getEntityByUuid(UUID uuid);
}
