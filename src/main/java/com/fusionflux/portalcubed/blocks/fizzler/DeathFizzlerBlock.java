package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.world.entity.Entity;

public class DeathFizzlerBlock extends AbstractFizzlerBlock {
	public DeathFizzlerBlock(Properties settings) {
		super(settings);
	}

	@Override
	public void applyEffectsTo(Entity entity) {
		fizzlePortals(entity);
		fizzlePhysicsEntity(entity);
		fizzleLiving(entity);
	}
}
