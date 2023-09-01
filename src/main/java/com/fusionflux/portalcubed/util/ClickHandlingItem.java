package com.fusionflux.portalcubed.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface ClickHandlingItem {

	InteractionResult onLeftClick(Player user, InteractionHand hand);
	InteractionResult onRightClick(Player user, InteractionHand hand);

}
