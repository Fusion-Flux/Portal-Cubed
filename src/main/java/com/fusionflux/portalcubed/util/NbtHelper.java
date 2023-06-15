package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class NbtHelper {
    public static void putVec3d(CompoundTag compoundTag, String name, Vec3 vec3d) {
        final ListTag list = new ListTag();
        list.add(DoubleTag.valueOf(vec3d.x));
        list.add(DoubleTag.valueOf(vec3d.y));
        list.add(DoubleTag.valueOf(vec3d.z));
        compoundTag.put(name, list);
    }

    public static Vec3 getVec3d(CompoundTag compoundTag, String name) {
        final ListTag list = compoundTag.getList(name, Tag.TAG_DOUBLE);
        return new Vec3(
            list.getDouble(0),
            list.getDouble(1),
            list.getDouble(2)
        );
    }

    public static void putQuaternion(CompoundTag compoundTag, String name, Quaternionf quat) {
        final ListTag list = new ListTag();
        list.add(FloatTag.valueOf(quat.x));
        list.add(FloatTag.valueOf(quat.y));
        list.add(FloatTag.valueOf(quat.z));
        list.add(FloatTag.valueOf(quat.w));
        compoundTag.put(name, list);
    }

    public static Quaternionf getQuaternion(CompoundTag compoundTag, String name) {
        final ListTag list = compoundTag.getList(name, Tag.TAG_FLOAT);
        final Quaternionf result = new Quaternionf(
            list.getFloat(0),
            list.getFloat(1),
            list.getFloat(2),
            list.getFloat(3)
        );
        if (result.lengthSquared() < 1e-7f) {
            result.identity(); // Just in case the quat ends up as (0, 0, 0, 0)
        }
        return result;
    }
}
