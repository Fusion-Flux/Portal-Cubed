package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.accessor.LevelExt;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class PortalCubedDamageSources {

    private final Registry<DamageType> damageTypes;

    private final DamageSource acid;
    private final DamageSource fizzle;
    private final DamageSource vaporization;
    private final DamageSource laser;
    private final DamageSource cube;

    public PortalCubedDamageSources(RegistryAccess registryAccess) {
        this.damageTypes = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        acid = source(PortalCubedDamageTypes.ACID);
        fizzle = source(PortalCubedDamageTypes.FIZZLE);
        vaporization = source(PortalCubedDamageTypes.VAPORIZATION);
        laser = source(PortalCubedDamageTypes.LASER);
        cube = source(PortalCubedDamageTypes.CUBE);
    }

    public static PortalCubedDamageSources pcSources(Level level) {
        return ((LevelExt)level).pcDamageSources();
    }

    private DamageSource source(ResourceKey<DamageType> key) {
        return new DamageSource(damageTypes.getHolderOrThrow(key));
    }

    public DamageSource acid() {
        return acid;
    }

    public DamageSource fizzle() {
        return fizzle;
    }

    public DamageSource vaporization() {
        return vaporization;
    }

    public DamageSource laser() {
        return laser;
    }

    public DamageSource cube() {
        return cube;
    }

}
