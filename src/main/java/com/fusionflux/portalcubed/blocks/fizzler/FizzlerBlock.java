package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.world.entity.Entity;

public class FizzlerBlock extends AbstractFizzlerBlock {
	public FizzlerBlock(Properties settings) {
		super(settings);
	}

	@Override
	public void applyEffectsTo(Entity entity) {
		fizzlePortals(entity);
		fizzlePhysicsEntity(entity);
	}
}
