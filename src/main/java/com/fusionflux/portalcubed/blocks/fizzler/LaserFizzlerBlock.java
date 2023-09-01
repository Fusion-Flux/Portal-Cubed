package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.world.entity.Entity;

public class LaserFizzlerBlock extends AbstractFizzlerBlock {
	public LaserFizzlerBlock(Properties settings) {
		super(settings);
	}

	@Override
	public void applyEffectsTo(Entity entity) {
		fizzleLiving(entity);
	}
}
