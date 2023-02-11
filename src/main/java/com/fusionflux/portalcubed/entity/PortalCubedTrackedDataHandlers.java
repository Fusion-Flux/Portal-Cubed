package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import java.util.Optional;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedTrackedDataHandlers {

    public static final TrackedDataHandler<Quaternion> QUATERNION = new TrackedDataHandler.SimpleHandler<>() {
        @Override
        public Quaternion read(PacketByteBuf buf) {
            return new Quaternion(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
        }

        @Override
        public void write(PacketByteBuf buf, Quaternion value) {
            buf.writeFloat(value.getX());
            buf.writeFloat(value.getY());
            buf.writeFloat(value.getZ());
            buf.writeFloat(value.getW());
        }
    };

    public static final TrackedDataHandler<Vec3d> VEC3D = new TrackedDataHandler.SimpleHandler<>() {
        @Override
        public Vec3d read(PacketByteBuf buf) {
            return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void write(PacketByteBuf buf, Vec3d value) {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        }
    };

    public static final TrackedDataHandler<Optional<Vec3d>> OPTIONAL_VEC3D = new TrackedDataHandler.SimpleHandler<>() {
        @Override
        public Optional<Vec3d> read(PacketByteBuf buf) {
            return buf.readOptional((innerBuf) -> new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }

        @Override
        public void write(PacketByteBuf buf, Optional<Vec3d> optionalValue) {
            buf.writeOptional(optionalValue, (innerBuf, value) -> {
                buf.writeDouble(value.x);
                buf.writeDouble(value.y);
                buf.writeDouble(value.z);
            });
        }
    };

    public static void register() {
        QuiltTrackedDataHandlerRegistry.register(id("quaternion"), QUATERNION);
        QuiltTrackedDataHandlerRegistry.register(id("vec3d"), VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("optional_vec3d"), OPTIONAL_VEC3D);
    }

}
