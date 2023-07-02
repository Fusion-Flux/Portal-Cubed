package com.fusionflux.portalcubed.util;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;

public class MutableVec3 {
    public double x, y, z;

    public MutableVec3() {
        this(0, 0, 0);
    }

    public MutableVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MutableVec3(Vec3 vec) {
        this(vec.x, vec.y, vec.z);
    }

    public void set(Axis axis, double value) {
        switch (axis) {
            case X -> this.x = value;
            case Y -> this.y = value;
            case Z -> this.z = value;
        }
    }

    public double get(Axis axis) {
        return axis.choose(x, y, z);
    }
}
