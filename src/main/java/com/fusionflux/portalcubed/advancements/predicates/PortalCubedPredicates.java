package com.fusionflux.portalcubed.advancements.predicates;

import net.minecraft.advancements.critereon.EntitySubPredicate;

public class PortalCubedPredicates {
    public static final EntitySubPredicate.Type ENERGY_PELLET = EnergyPelletPredicate::fromJson;

    public static void register() {
        EntitySubPredicate.Types.TYPES.put("portalcubed:energy_pellet", ENERGY_PELLET);
    }
}
