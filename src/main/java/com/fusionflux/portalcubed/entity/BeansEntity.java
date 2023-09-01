package com.fusionflux.portalcubed.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class BeansEntity extends CorePhysicsEntity  {
	public BeansEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

}
