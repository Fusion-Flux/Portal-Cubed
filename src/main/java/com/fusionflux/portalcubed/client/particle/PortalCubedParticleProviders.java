package com.fusionflux.portalcubed.client.particle;

import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class PortalCubedParticleProviders {
    public static void register() {
        final ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(PortalCubedParticleTypes.DECAL, DecalParticle.Provider::new);
        registry.register(
            PortalCubedParticleTypes.ENERGY_SPARK,
            sprites -> (type, level, x, y, z, xSpeed, ySpeed, zSpeed) ->
                new EnergySparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed)
        );
    }
}
