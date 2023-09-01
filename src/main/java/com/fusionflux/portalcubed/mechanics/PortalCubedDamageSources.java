package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.entity.EnergyPellet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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

	private DamageSource source(ResourceKey<DamageType> damageTypeKey, @Nullable Entity entity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(damageTypeKey), entity);
	}

	private DamageSource source(ResourceKey<DamageType> damageTypeKey, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(damageTypeKey), causingEntity, directEntity);
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

	public DamageSource vaporization(EnergyPellet pellet, @Nullable Entity thrower) {
		return source(PortalCubedDamageTypes.VAPORIZATION, pellet, thrower);
	}

	public DamageSource laser() {
		return laser;
	}

	public DamageSource cube() {
		return cube;
	}

}
