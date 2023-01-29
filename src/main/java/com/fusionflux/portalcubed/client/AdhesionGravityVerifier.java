package com.fusionflux.portalcubed.client;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.packet.UpdateGravityPacket;
import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

public class AdhesionGravityVerifier {
    public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(PortalCubed.MOD_ID, "adhesion_gel");
    public static int FIELD_GRAVITY_PRIORITY = 10;
    public static int FIELD_GRAVITY_MAX_DURATION = 2;


    public static Gravity newFieldGravity(Direction direction){
        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(),new RotationParameters().rotateVelocity(true).alternateCenter(true));
    }

    public static boolean check(ServerPlayerEntity player, PacketByteBuf info, UpdateGravityPacket packet) {
        if (packet.gravity.duration() > FIELD_GRAVITY_MAX_DURATION) return false;

        if(packet.gravity.priority() > FIELD_GRAVITY_PRIORITY) return false;

        if(!packet.gravity.source().equals(FIELD_GRAVITY_SOURCE.toString())) return false;

        if(packet.gravity.direction() == null) return false;
        info.readBlockPos();
        World world = player.getWorld();
        /*Return true if the block is a field generator or plating and could have triggered the gravity change.*/
        return world != null;
    }

    public static PacketByteBuf packInfo(BlockPos block){
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(block);
        return buf;
    }

}
