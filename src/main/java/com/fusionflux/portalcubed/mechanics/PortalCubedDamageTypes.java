package com.fusionflux.portalcubed.mechanics;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import static com.fusionflux.portalcubed.PortalCubed.id;

public interface PortalCubedDamageTypes {
	ResourceKey<DamageType> ACID = ResourceKey.create(Registries.DAMAGE_TYPE, id("acid"));
	ResourceKey<DamageType> FIZZLE = ResourceKey.create(Registries.DAMAGE_TYPE, id("fizzle"));
	ResourceKey<DamageType> VAPORIZATION = ResourceKey.create(Registries.DAMAGE_TYPE, id("vaporization"));
	ResourceKey<DamageType> LASER = ResourceKey.create(Registries.DAMAGE_TYPE, id("laser"));
	ResourceKey<DamageType> CUBE = ResourceKey.create(Registries.DAMAGE_TYPE, id("cube"));
}
