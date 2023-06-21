package com.fusionflux.portalcubed.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedParticleTypes {
    public static final ParticleType<DecalParticleOption> DECAL = register(
        "decal", false, DecalParticleOption.PARAMETERS_FACTORY, DecalParticleOption::codec
    );

    @SuppressWarnings("deprecation")
    private static <T extends ParticleOptions> ParticleType<T> register(
        String name, boolean alwaysShow, ParticleOptions.Deserializer<T> parameterFactory, Function<ParticleType<T>, Codec<T>> codecProvider
    ) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id(name), new ParticleType<T>(alwaysShow, parameterFactory) {
            @NotNull
            @Override
            public Codec<T> codec() {
                return codecProvider.apply(this);
            }
        });
    }

    public static void register() {
    }
}
