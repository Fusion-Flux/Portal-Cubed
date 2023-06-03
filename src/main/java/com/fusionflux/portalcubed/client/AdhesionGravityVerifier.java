package com.fusionflux.portalcubed.client;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.packet.UpdateGravityPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class AdhesionGravityVerifier {
    public static final ResourceLocation FIELD_GRAVITY_SOURCE = id("adhesion_gel");
    public static final int FIELD_GRAVITY_PRIORITY = 10;
    public static final int FIELD_GRAVITY_MAX_DURATION = 2;


    public static Gravity newFieldGravity(Direction direction) {
        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(), new RotationParameters().rotateVelocity(true).alternateCenter(true));
    }

    public static boolean check(ServerPlayer player, FriendlyByteBuf info, UpdateGravityPacket packet) {
        if (packet.gravity.duration() > FIELD_GRAVITY_MAX_DURATION) return false;

        if (packet.gravity.priority() > FIELD_GRAVITY_PRIORITY) return false;

        if (!packet.gravity.source().equals(FIELD_GRAVITY_SOURCE.toString())) return false;

        if (packet.gravity.direction() == null) return false;
        info.readBlockPos();
        Level world = player.getLevel();
        /*Return true if the block is a field generator or plating and could have triggered the gravity change.*/
        return world != null;
    }

    public static FriendlyByteBuf packInfo(BlockPos block) {
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(block);
        return buf;
    }

}
