package com.fusionflux.portalcubed.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
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

    @Nullable
    public static BlockPos readBlockPos(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND))
            return null;
        CompoundTag pos = tag.getCompound(key);
        if (pos.contains("X", Tag.TAG_INT) && pos.contains("Y", Tag.TAG_INT) && pos.contains("Z", Tag.TAG_INT))
            return new BlockPos(pos.getInt("X"), pos.getInt("Y"), pos.getInt("Z"));
        return null;
    }

    public static <T extends Enum<T>> T readEnum(CompoundTag tag, String key, T fallback) {
        if (!tag.contains(key, Tag.TAG_STRING))
            return fallback;
        String name = tag.getString(key);
        //noinspection unchecked
        Class<T> clazz = (Class<T>) fallback.getClass();
        for (T entry : clazz.getEnumConstants()) {
            if (entry.name().equals(name))
                return entry;
        }
        return fallback;
    }
}
