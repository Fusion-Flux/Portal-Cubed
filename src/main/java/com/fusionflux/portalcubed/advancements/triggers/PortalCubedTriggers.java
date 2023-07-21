package com.fusionflux.portalcubed.advancements.triggers;

import net.minecraft.advancements.CriteriaTriggers;

public class PortalCubedTriggers {
    public static final BounceTrigger BOUNCE = new BounceTrigger();

    public static void register() {
        CriteriaTriggers.register(BOUNCE);
    }
}
