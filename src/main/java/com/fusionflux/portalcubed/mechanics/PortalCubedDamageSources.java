package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.entity.damage.DamageSource;

public class PortalCubedDamageSources {

    public static final DamageSource ACID = source("acid").setBypassesArmor();
    public static final DamageSource FIZZLE = source("fizzle").setBypassesArmor();
    public static final DamageSource VAPORIZATION = source("vaporization").setBypassesArmor();

    private static DamageSource source(String id) {
        return new DamageSource(PortalCubed.MOD_ID + '.' + id);
    }

}
