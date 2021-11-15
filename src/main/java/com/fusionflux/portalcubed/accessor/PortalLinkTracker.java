package com.fusionflux.portalcubed.accessor;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class PortalLinkTracker implements TrackedDataHandler<UUID> {

    @Override
    public void write(PacketByteBuf data, UUID object) {
        data.writeUuid(object);
    }

    @Override
    public UUID read(PacketByteBuf packetByteBuf) {
        return null;
    }

    @Override
    public UUID copy(UUID object) {
        return null;
    }
}
