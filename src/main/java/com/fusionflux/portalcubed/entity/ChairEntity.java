package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.util.PortalCubedComponents;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ChairEntity extends CorePhysicsEntity  {
	public ChairEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

	@Override
	protected InteractionResult physicsEntityInteraction(Player player, InteractionHand hand) {
		if (isVehicle() || level().isClientSide())
			return InteractionResult.PASS;
		CorePhysicsEntity heldEntity = PortalCubedComponents.HOLDER_COMPONENT.get(player).entityBeingHeld();
		if (heldEntity == this)
			return InteractionResult.PASS;

		player.startRiding(this);
		return InteractionResult.SUCCESS;
	}
}
