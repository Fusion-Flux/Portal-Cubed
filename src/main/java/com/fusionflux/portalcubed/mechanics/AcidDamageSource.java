package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.PortalCubed;

import net.minecraft.entity.damage.DamageSource;

public class AcidDamageSource extends DamageSource {

    public static final DamageSource INSTANCE = new AcidDamageSource().setBypassesArmor();

    public AcidDamageSource() {
        super(PortalCubed.MODID + ".acid");
    }

}
