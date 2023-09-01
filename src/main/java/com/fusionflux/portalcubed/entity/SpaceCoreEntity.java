package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class SpaceCoreEntity extends CorePhysicsEntity {

	public SpaceCoreEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

	private int t = 0;

	@Override
	public void tick() {
		if (!this.level().isClientSide) {
			if (t == 0) {
				level().playSound(null, this, PortalCubedSounds.SPACE_CORE_EVENT, this.getSoundSource(), 1f, 1f);
				t = 2765;
			}
			t--;
		}
		super.tick();
	}
}
