package com.fusionflux.portalcubed.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class RedirectionCubeEntity extends StorageCubeEntity {
    private int activeTicks;

    public RedirectionCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    public RedirectionCubeEntity getConnection() {
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        activeTicks = Math.max(activeTicks - 1, 0);
    }

    public void markActive() {
        activeTicks = 2;
    }

    public boolean isActive() {
        return activeTicks > 0;
    }

}
