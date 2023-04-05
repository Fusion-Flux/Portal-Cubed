package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class TurretEntity extends CorePhysicsEntity {
//    private static final Box BASE_BOX = createFootBox();

    public TurretEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

//    @Override
//    protected Box calculateBoundingBox() {
//        return super.calculateBoundingBox();
//    }
}
