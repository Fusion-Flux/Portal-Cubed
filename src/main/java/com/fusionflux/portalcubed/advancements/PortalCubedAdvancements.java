package com.fusionflux.portalcubed.advancements;

import com.fusionflux.portalcubed.advancements.conditions.PortalCubedConditions;
import com.fusionflux.portalcubed.advancements.predicates.PortalCubedPredicates;
import com.fusionflux.portalcubed.advancements.triggers.PortalCubedTriggers;

public class PortalCubedAdvancements {
    public static void register() {
        PortalCubedConditions.register();
        PortalCubedPredicates.register();
        PortalCubedTriggers.register();
    }
}
