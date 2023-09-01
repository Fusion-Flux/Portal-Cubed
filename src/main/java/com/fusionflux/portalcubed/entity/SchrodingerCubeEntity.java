package com.fusionflux.portalcubed.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

public class SchrodingerCubeEntity extends RedirectionCubeEntity {

	public SchrodingerCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

	@Override
	public SchrodingerCubeEntity getConnection() {
		return Objects.requireNonNullElse(level().getNearestEntity(
			SchrodingerCubeEntity.class,
			TargetingConditions.forNonCombat()
				.selector(e -> Objects.equals(e.getCustomName(), getCustomName()))
				.ignoreLineOfSight()
				.ignoreInvisibilityTesting(),
			this,
			getX(), getY(), getZ(),
			AABB.ofSize(position(), 256, 256, 256)
		), this);
	}

}
