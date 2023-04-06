package com.fusionflux.portalcubed.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedParticleTypes {
    public static final ParticleType<DecalParticleEffect> DECAL = register(
        "decal", false, DecalParticleEffect.PARAMETERS_FACTORY, DecalParticleEffect::codec
    );

    @SuppressWarnings("deprecation")
    private static <T extends ParticleEffect> ParticleType<T> register(
        String name, boolean alwaysShow, ParticleEffect.Factory<T> parameterFactory, Function<ParticleType<T>, Codec<T>> codecProvider
    ) {
        return Registry.register(Registry.PARTICLE_TYPE, id(name), new ParticleType<T>(alwaysShow, parameterFactory) {
            @Override
            public Codec<T> getCodec() {
                return codecProvider.apply(this);
            }
        });
    }

    public static void register() {
    }
}
