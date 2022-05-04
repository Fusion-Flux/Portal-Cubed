package com.fusionflux.portalcubed.util;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CustomTrackedData {
    public static final TrackedDataHandler<Double> DOUBLE = new TrackedDataHandler<Double>() {
        public void write(PacketByteBuf packetByteBuf, Double integer) {
            packetByteBuf.writeDouble(integer);
        }

        public Double read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readDouble();
        }

        public Double copy(Double integer) {
            return integer;
        }
    };


    public static final TrackedDataHandler<Vec3d> VEC3D = new TrackedDataHandler<Vec3d>() {
        public void write(PacketByteBuf packetByteBuf, Vec3d Vec3d) {
            packetByteBuf.writeDouble(Vec3d.getX());
            packetByteBuf.writeDouble(Vec3d.getY());
            packetByteBuf.writeDouble(Vec3d.getZ());
        }

        public Vec3d read(PacketByteBuf packetByteBuf) {
            return new Vec3d(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble());
        }

        public Vec3d copy(Vec3d Vec3d) {
            return Vec3d;
        }
    };

    static {
        TrackedDataHandlerRegistry.register(VEC3D);
        TrackedDataHandlerRegistry.register(DOUBLE);
    }
}
