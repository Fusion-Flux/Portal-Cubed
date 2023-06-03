package com.fusionflux.portalcubed.particle;

import com.fusionflux.portalcubed.commands.DirectionArgumentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class DecalParticleEffect implements ParticleOptions {
    public static final ResourceLocation BULLET_HOLE_CONCRETE = id("particle/bullet_hole_concrete");
    public static final ResourceLocation BULLET_HOLE_GLASS = id("particle/bullet_hole_glass");
    public static final ResourceLocation BULLET_HOLE_METAL = id("particle/bullet_hole_metal");
    public static final ResourceLocation SCORCH = id("particle/scorch");

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<DecalParticleEffect> PARAMETERS_FACTORY = new Deserializer<>() {
        @Override
        public DecalParticleEffect fromCommand(ParticleType<DecalParticleEffect> type, StringReader reader) throws CommandSyntaxException {
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
            return new DecalParticleEffect(type, texture, direction, multiply);
        }

        @Override
        public DecalParticleEffect fromNetwork(ParticleType<DecalParticleEffect> type, FriendlyByteBuf buf) {
            return new DecalParticleEffect(
                type,
                buf.readResourceLocation(),
                buf.readEnum(Direction.class),
                buf.readBoolean()
            );
        }
    };

    private final ParticleType<DecalParticleEffect> particleType;
    private final ResourceLocation texture;
    private final Direction direction;
    private final boolean multiply;

    public DecalParticleEffect(ParticleType<DecalParticleEffect> particleType, ResourceLocation texture, Direction direction, boolean multiply) {
        this.particleType = particleType;
        this.texture = texture;
        this.direction = direction;
        this.multiply = multiply;
    }

    public DecalParticleEffect(ResourceLocation texture, Direction direction, boolean multiply) {
        this(PortalCubedParticleTypes.DECAL, texture, direction, multiply);
    }

    public DecalParticleEffect(ResourceLocation texture, Direction direction) {
        this(texture, direction, false);
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(texture);
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(particleType) + " " + texture;
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

    public static Codec<DecalParticleEffect> codec(ParticleType<DecalParticleEffect> particleType) {
        return RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(DecalParticleEffect::getTexture),
                Direction.CODEC.fieldOf("direction").forGetter(DecalParticleEffect::getDirection),
                Codec.BOOL.fieldOf("multiply").forGetter(DecalParticleEffect::isMultiply)
            ).apply(instance, (texture, direction, multiply) ->
                new DecalParticleEffect(particleType, texture, direction, multiply)
            )
        );
    }
}
