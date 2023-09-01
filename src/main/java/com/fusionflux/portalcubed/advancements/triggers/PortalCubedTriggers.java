package com.fusionflux.portalcubed.advancements.triggers;

import net.minecraft.advancements.CriteriaTriggers;

public class PortalCubedTriggers {
	public static final BounceTrigger BOUNCE = new BounceTrigger();
	public static final FlingTrigger FLING = new FlingTrigger();

	public static void register() {
		CriteriaTriggers.register(BOUNCE);
		CriteriaTriggers.register(FLING);
	}
}
