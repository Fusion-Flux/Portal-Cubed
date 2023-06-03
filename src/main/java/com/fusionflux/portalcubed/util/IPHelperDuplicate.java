package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

// Both methods are from https://github.com/qouteall/ImmersivePortalsMod/blob/1.18/q_misc_util/src/main/java/qouteall/q_misc_util/Helper.java
public class IPHelperDuplicate {
    public static void putVec3d(CompoundTag compoundTag, String name, Vec3 vec3d) {
        compoundTag.putDouble(name + "X", vec3d.x);
        compoundTag.putDouble(name + "Y", vec3d.y);
        compoundTag.putDouble(name + "Z", vec3d.z);
    }

    public static Vec3 getVec3d(CompoundTag compoundTag, String name) {
        return new Vec3(
                compoundTag.getDouble(name + "X"),
                compoundTag.getDouble(name + "Y"),
                compoundTag.getDouble(name + "Z")
        );
    }
}
