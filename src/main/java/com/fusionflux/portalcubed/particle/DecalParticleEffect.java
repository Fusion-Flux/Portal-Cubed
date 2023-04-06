package com.fusionflux.portalcubed.particle;

import com.fusionflux.portalcubed.commands.DirectionArgumentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class DecalParticleEffect implements ParticleEffect {
    public static final Identifier SCORCH = id("particle/scorch");
    public static final Identifier BULLET_HOLE_CONCRETE = id("particle/bullet_hole_concrete");

    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<DecalParticleEffect> PARAMETERS_FACTORY = new Factory<>() {
        @Override
        public DecalParticleEffect read(ParticleType<DecalParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            final Identifier texture = IdentifierArgumentType.identifier().parse(reader);
            reader.expect(' ');
            final Direction direction = DirectionArgumentType.direction().parse(reader);
            return new DecalParticleEffect(type, texture, direction);
        }

        @Override
        public DecalParticleEffect read(ParticleType<DecalParticleEffect> type, PacketByteBuf buf) {
            return new DecalParticleEffect(type, buf.readIdentifier(), buf.readEnumConstant(Direction.class));
        }
    };

    private final ParticleType<DecalParticleEffect> particleType;
    private final Identifier texture;
    private final Direction direction;

    public DecalParticleEffect(ParticleType<DecalParticleEffect> particleType, Identifier texture, Direction direction) {
        this.particleType = particleType;
        this.texture = texture;
        this.direction = direction;
    }

    public DecalParticleEffect(Identifier texture, Direction direction) {
        this(PortalCubedParticleTypes.DECAL, texture, direction);
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(texture);
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(particleType) + " " + texture;
    }

    public Identifier getTexture() {
        return texture;
    }

    public Direction getDirection() {
        return direction;
    }

    public static Codec<DecalParticleEffect> codec(ParticleType<DecalParticleEffect> particleType) {
        return RecordCodecBuilder.create(
            instance -> instance.group(
                Identifier.CODEC.fieldOf("texture").forGetter(DecalParticleEffect::getTexture),
                Direction.CODEC.fieldOf("direction").forGetter(DecalParticleEffect::getDirection)
            ).apply(instance, (texture, direction) -> new DecalParticleEffect(particleType, texture, direction))
        );
    }
}
