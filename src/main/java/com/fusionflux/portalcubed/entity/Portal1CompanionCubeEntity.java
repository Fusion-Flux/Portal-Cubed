package com.fusionflux.portalcubed.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class Portal1CompanionCubeEntity extends CorePhysicsEntity {
	public Portal1CompanionCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

}
