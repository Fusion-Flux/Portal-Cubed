package com.fusionflux.portalcubed.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import com.fusionflux.portalcubed.util.LerpedQuaternion;
import org.joml.Quaternionf;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import java.util.Optional;

import static com.fusionflux.portalcubed.PortalCubed.id;
import static net.minecraft.network.syncher.EntityDataSerializers.QUATERNION;

public class PortalCubedTrackedDataHandlers {

    public static final EntityDataSerializer<LerpedQuaternion> LERPED_QUAT = EntityDataSerializer.simple(
            (buf, quat) -> quat.toNetwork(buf),
            LerpedQuaternion::fromNetwork
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
    public static final EntityDataSerializer<Optional<Quaternionf>> OPTIONAL_QUAT = EntityDataSerializer.optional(QUATERNION::write, QUATERNION::read);

    public static final EntityDataSerializer<ResourceLocation> IDENTIFIER = EntityDataSerializer.simple(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);

    public static void register() {
        QuiltTrackedDataHandlerRegistry.register(id("lerped_quaternion"), LERPED_QUAT);
        QuiltTrackedDataHandlerRegistry.register(id("optional_quat"), OPTIONAL_QUAT);
        QuiltTrackedDataHandlerRegistry.register(id("vec3d"), VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("optional_vec3d"), OPTIONAL_VEC3D);
        QuiltTrackedDataHandlerRegistry.register(id("identifier"), IDENTIFIER);
    }

}
