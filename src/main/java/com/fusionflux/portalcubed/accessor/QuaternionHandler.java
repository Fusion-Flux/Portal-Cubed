package com.fusionflux.portalcubed.accessor;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

public class QuaternionHandler implements TrackedDataHandler<Quaternion> {
    public static final QuaternionHandler QUATERNION_HANDLER = new QuaternionHandler();

    @Override
    public void write(PacketByteBuf buf, Quaternion quat) {
        buf.writeFloat(quat.getX());
        buf.writeFloat(quat.getY());
        buf.writeFloat(quat.getZ());
        buf.writeFloat(quat.getW());
    }

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
    public Quaternion copy(Quaternion quat) {
        return new Quaternion(quat);
    }


}
