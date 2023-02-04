package com.fusionflux.portalcubed.entity;

import java.util.Optional;

import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import com.fusionflux.portalcubed.PortalCubed;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class PortalCubedTrackedDataHandlers {

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
        QuiltTrackedDataHandlerRegistry.register(PortalCubed.id("vec3d"), VEC3D);
        QuiltTrackedDataHandlerRegistry.register(PortalCubed.id("optional_vec3d"), OPTIONAL_VEC3D);
    }

}
