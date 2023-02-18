package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import java.util.Optional;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedTrackedDataHandlers {

    public static final TrackedDataHandler<Quaternion> QUATERNION = TrackedDataHandler.create(
        (buf, value) -> {
            buf.writeFloat(value.getX());
            buf.writeFloat(value.getY());
            buf.writeFloat(value.getZ());
            buf.writeFloat(value.getW());
        },
        buf -> new Quaternion(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );

    public static final TrackedDataHandler<Vec3d> VEC3D = TrackedDataHandler.create(
        (buf, value) -> {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        },
        buf -> new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
    );

    public static final TrackedDataHandler<Optional<Vec3d>> OPTIONAL_VEC3D = TrackedDataHandler.createOptional(VEC3D::write, VEC3D::read);

    public static void register() {
        QuiltTrackedDataHandlerRegistry.register(id("quaternion"), QUATERNION);
        QuiltTrackedDataHandlerRegistry.register(id("vec3d"), VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("optional_vec3d"), OPTIONAL_VEC3D);
    }

}
