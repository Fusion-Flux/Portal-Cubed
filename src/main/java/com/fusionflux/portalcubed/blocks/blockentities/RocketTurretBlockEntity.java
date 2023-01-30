package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.UUID;

public class RocketTurretBlockEntity extends BlockEntity {
    public static final int UPDATE_ANGLE = 0;
    public static final int UPDATE_LOCKED_TICKS = 1;

    public static final int LOCK_TICKS = 25;

    private float yaw, pitch;
    private int lockedTicks;
    private UUID rocketUuid = Util.NIL_UUID;

    public RocketTurretBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public RocketTurretBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
        nbt.putFloat("LockedTicks", lockedTicks);
        nbt.putUuid("RocketUUID", rocketUuid);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        yaw = nbt.getFloat("Yaw");
        pitch = nbt.getFloat("Pitch");
        lockedTicks = nbt.getInt("LockedTicks");
        rocketUuid = nbt.getUuid("RocketUUID");
    }

    public void setAngle(Pair<Float, Float> angle) {
        yaw = angle.getLeft();
        pitch = angle.getRight();
    }

    public void setLockedTicks(int lockedTicks) {
        this.lockedTicks = lockedTicks;
    }

    public void fire() {
        if (world == null || world.isClient) return;
        // TODO: Implement
        PortalCubed.LOGGER.info("BAM");
    }

    private void syncLockedTicks() {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeByte(UPDATE_LOCKED_TICKS);
        buf.writeVarInt(lockedTicks);
        sendSyncPacket(buf);
    }

    private void syncAngle() {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeByte(UPDATE_ANGLE);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        sendSyncPacket(buf);
    }

    private void sendSyncPacket(PacketByteBuf buf) {
        if (world == null || world.isClient) {
            PortalCubed.LOGGER.warn("Shouldn't have called sendSyncPacket from the client");
            return;
        }
        final ServerWorld serverWorld = (ServerWorld)world;
        final int viewDistance = serverWorld.getServer().getPlayerManager().getViewDistance() * 16 + 1;
        for (final ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (player.getBlockPos().isWithinDistance(getPos(), viewDistance)) {
                ServerPlayNetworking.send(player, PortalCubedClientPackets.ROCKET_TURRET_UPDATE_PACKET, buf);
            }
        }
    }

    public State getState() {
        if (lockedTicks == 0) {
            return State.SEARCHING;
        }
        if (lockedTicks <= LOCK_TICKS) {
            return State.LOCKED;
        }
        return State.FIRING;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (lockedTicks > 0) {
            if (lockedTicks++ == LOCK_TICKS) {
                fire();
                syncLockedTicks();
            } else if (
                lockedTicks > LOCK_TICKS &&
                    world instanceof ServerWorld serverWorld &&
                    serverWorld.getEntity(rocketUuid) == null
            ) {
                lockedTicks = 0;
                rocketUuid = Util.NIL_UUID;
                syncLockedTicks();
            }
            return;
        }
        final PlayerEntity player = world.getClosestPlayer(
            TargetPredicate.createNonAttackable(),
            pos.getX(), pos.getY(), pos.getZ()
        );
        if (player == null) return;
        final Vec3d offset = player.getPos().subtract(Vec3d.ofCenter(getPos().up()));
        final float newYaw = MathHelper.lerp(
            0.5f, yaw,
            (float)Math.toDegrees(MathHelper.atan2(offset.z, offset.x))
        );
        final float newPitch = MathHelper.lerp(
            0.5f, pitch,
            (float)Math.toDegrees(Math.asin(-offset.y))
        );
        if (Math.max(Math.abs(newYaw - yaw), Math.abs(newPitch - pitch)) <= 15) {
            lockedTicks++;
            syncLockedTicks();
            // Also play rocket_locking_beep1
        }
        yaw = newYaw;
        pitch = newPitch;
        syncAngle();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }

    public enum State {
        SEARCHING,
        LOCKED,
        FIRING
    }
}
