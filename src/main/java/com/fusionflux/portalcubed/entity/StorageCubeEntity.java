package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StorageCubeEntity extends CorePhysicsEntity  {

	public StorageCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

	@Override
	public LivingEntity.@NotNull Fallsounds getFallSounds() {
		return new LivingEntity.Fallsounds(PortalCubedSounds.CUBE_LOW_HIT_EVENT, PortalCubedSounds.CUBE_HIGH_HIT_EVENT);
	}

	private int buttonTimer = 0;

	public void setButtonTimer(int time) {
		buttonTimer = time;
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide) {
			if (buttonTimer <= 0) {
				setOnButton(false);
			} else {
				setOnButton(true);
				buttonTimer -= 1;
			}
		}
	}

}
