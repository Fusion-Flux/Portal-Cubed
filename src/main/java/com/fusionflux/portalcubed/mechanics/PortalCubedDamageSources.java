package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.world.damagesource.DamageSource;

public class PortalCubedDamageSources {

    public static final DamageSource ACID = source("acid").bypassArmor();
    public static final DamageSource FIZZLE = source("fizzle").bypassArmor();
    public static final DamageSource VAPORIZATION = source("vaporization").bypassArmor();
    public static final DamageSource LASER = source("laser").bypassArmor().setIsFire();
    public static final DamageSource CUBE = source("cube").damageHelmet();

    private static DamageSource source(String id) {
        return new DamageSource(PortalCubed.MOD_ID + '.' + id);
    }

}
