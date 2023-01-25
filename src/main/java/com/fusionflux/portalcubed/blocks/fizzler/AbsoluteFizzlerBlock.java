package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.UUID;

public class AbsoluteFizzlerBlock extends AbstractFizzlerBlock implements BlockCollisionTrigger {
    public AbsoluteFizzlerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityEnter(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            for (final UUID portal : CalledValues.getPortals(entity)) {
                final Entity checkPortal = ((ServerWorld)world).getEntity(portal);
                if (checkPortal != null) {
                    checkPortal.kill();
                }
            }
            if (entity instanceof CorePhysicsEntity physicsEntity) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), PortalCubedSounds.MATERIAL_EMANCIPATION_EVENT, SoundCategory.NEUTRAL, 0.1f, 1f);
                physicsEntity.setNoGravity(true);
                physicsEntity.startFizzling();
                final PacketByteBuf buf = PacketByteBufs.create();
                buf.writeVarInt(entity.getId());
                final Packet<?> packet = ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.FIZZLE_PACKET, buf);
                for (final ServerPlayerEntity player : physicsEntity.getTracking()) {
                    player.networkHandler.sendPacket(packet);
                }
            } else if (entity instanceof PlayerEntity player) {
                player.playSound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundCategory.NEUTRAL, 0.5f, 1f);
            }
        }
    }
}
