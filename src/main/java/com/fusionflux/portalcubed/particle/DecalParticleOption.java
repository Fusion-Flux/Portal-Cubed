package com.fusionflux.portalcubed.particle;

import com.fusionflux.portalcubed.commands.DirectionArgumentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class DecalParticleOption implements ParticleOptions {
    public static final ResourceLocation BULLET_HOLE_CONCRETE = id("bullet_hole_concrete");
    public static final ResourceLocation BULLET_HOLE_GLASS = id("bullet_hole_glass");
    public static final ResourceLocation BULLET_HOLE_METAL = id("bullet_hole_metal");
    public static final ResourceLocation SCORCH = id("scorch");

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<DecalParticleOption> PARAMETERS_FACTORY = new Deserializer<>() {
        @NotNull
        @Override
        public DecalParticleOption fromCommand(ParticleType<DecalParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            final ResourceLocation texture = ResourceLocationArgument.id().parse(reader);
            reader.expect(' ');
            final Direction direction = DirectionArgumentType.direction().parse(reader);
            final boolean multiply;
            if (reader.canRead()) {
                reader.expect(' ');
                multiply = reader.readBoolean();
            } else {
                multiply = false;
            }
            return new DecalParticleOption(type, texture, direction, multiply);
        }

        @NotNull
        @Override
        public DecalParticleOption fromNetwork(ParticleType<DecalParticleOption> type, FriendlyByteBuf buf) {
            return new DecalParticleOption(
                type,
                buf.readResourceLocation(),
                buf.readEnum(Direction.class),
                buf.readBoolean()
            );
        }
    };

    private final ParticleType<DecalParticleOption> particleType;
    private final ResourceLocation texture;
    private final Direction direction;
    private final boolean multiply;

    public DecalParticleOption(ParticleType<DecalParticleOption> particleType, ResourceLocation texture, Direction direction, boolean multiply) {
        this.particleType = particleType;
        this.texture = texture;
        this.direction = direction;
        this.multiply = multiply;
    }

    public DecalParticleOption(ResourceLocation texture, Direction direction, boolean multiply) {
        this(PortalCubedParticleTypes.DECAL, texture, direction, multiply);
    }

    public DecalParticleOption(ResourceLocation texture, Direction direction) {
        this(texture, direction, false);
    }

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(texture);
        buf.writeEnum(direction);
        buf.writeBoolean(multiply);
    }

    @NotNull
    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(particleType) + " " + texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMultiply() {
        return multiply;
    }

    public static Codec<DecalParticleOption> codec(ParticleType<DecalParticleOption> particleType) {
        return RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(DecalParticleOption::getTexture),
                Direction.CODEC.fieldOf("direction").forGetter(DecalParticleOption::getDirection),
                Codec.BOOL.fieldOf("multiply").forGetter(DecalParticleOption::isMultiply)
            ).apply(instance, (texture, direction, multiply) ->
                new DecalParticleOption(particleType, texture, direction, multiply)
            )
        );
    }
}
