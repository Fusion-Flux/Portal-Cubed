package com.fusionflux.portalcubed.entity;

import com.mojang.math.Quaternion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import java.util.Optional;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedTrackedDataHandlers {

    public static final EntityDataSerializer<Quaternion> QUATERNION = EntityDataSerializer.simple(
        (buf, value) -> {
            buf.writeFloat(value.i());
            buf.writeFloat(value.j());
            buf.writeFloat(value.k());
            buf.writeFloat(value.r());
        },
        buf -> new Quaternion(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );

    public static final EntityDataSerializer<Vec3> VEC3D = EntityDataSerializer.simple(
        (buf, value) -> {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        },
        buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
    );

    public static final EntityDataSerializer<Optional<Vec3>> OPTIONAL_VEC3D = EntityDataSerializer.optional(VEC3D::write, VEC3D::read);

    public static final EntityDataSerializer<ResourceLocation> IDENTIFIER = EntityDataSerializer.simple(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);

    public static void register() {
        QuiltTrackedDataHandlerRegistry.register(id("quaternion"), QUATERNION);
        QuiltTrackedDataHandlerRegistry.register(id("vec3d"), VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("optional_vec3d"), OPTIONAL_VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("identifier"), IDENTIFIER);
    }

}
