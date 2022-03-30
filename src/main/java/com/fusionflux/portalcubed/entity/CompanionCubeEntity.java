package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class CompanionCubeEntity extends StorageCubeEntity  {
    public CompanionCubeEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }
}
