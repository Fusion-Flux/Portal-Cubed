package com.fusionflux.portalcubed.client.particle;

import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class PortalCubedParticleFactories {
    public static void register() {
        final ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(PortalCubedParticleTypes.DECAL, DecalParticle.Factory::new);
    }
}
