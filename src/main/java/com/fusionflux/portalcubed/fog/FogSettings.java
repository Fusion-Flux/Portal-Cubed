package com.fusionflux.portalcubed.fog;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Locale;
import java.util.Optional;

public record FogSettings(float start, float end, Color color, Shape shape) {
    public record Color(int r, int g, int b) {
    }

    public enum Shape implements StringIdentifiable {
        SPHERE, CYLINDER;

        private final String id = name().toLowerCase(Locale.ROOT);

        @Override
        public String asString() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

        @ClientOnly
        public com.mojang.blaze3d.shader.FogShape toBlaze3d() {
            return com.mojang.blaze3d.shader.FogShape.values()[ordinal()];
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putFloat("Start", start);
        nbt.putFloat("End", end);
        nbt.putByteArray("Color", new byte[] {(byte)color.r, (byte)color.g, (byte)color.b});
        nbt.putByte("Shape", (byte)shape.ordinal());
        return nbt;
    }

    public static FogSettings readNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) {
            return null;
        }
        final byte[] color = nbt.getByteArray("Color");
        return new FogSettings(
            nbt.getFloat("Start"),
            nbt.getFloat("End"),
            new Color(color[0] & 0xff, color[1] & 0xff, color[2] & 0xff),
            Shape.values()[nbt.getByte("Shape") & 0xff]
        );
    }

    public void encode(PacketByteBuf buf) {
        buf.writeFloat(start);
        buf.writeFloat(end);
        buf.writeByte(color.r);
        buf.writeByte(color.g);
        buf.writeByte(color.b);
        buf.writeEnumConstant(shape);
    }

    public static void encodeOptional(@Nullable FogSettings settings, PacketByteBuf buf) {
        buf.writeOptional(Optional.ofNullable(settings), (buf1, settings1) -> settings1.encode(buf1));
    }

    @NotNull
    public static FogSettings decode(PacketByteBuf buf) {
        return new FogSettings(
            buf.readFloat(),
            buf.readFloat(),
            new Color(buf.readByte() & 0xff, buf.readByte() & 0xff, buf.readByte() & 0xff),
            buf.readEnumConstant(Shape.class)
        );
    }

    @Nullable
    public static FogSettings decodeOptional(PacketByteBuf buf) {
        return buf.readOptional(FogSettings::decode).orElse(null);
    }
}
