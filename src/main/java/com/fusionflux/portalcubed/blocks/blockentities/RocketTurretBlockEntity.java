package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.UUID;

import static com.fusionflux.portalcubed.blocks.RocketTurretBlock.POWERED;

public class RocketTurretBlockEntity extends BlockEntity {
    public static final int UPDATE_ANGLE = 0;
    public static final int UPDATE_LOCKED_TICKS = 1;

    public static final int LOCK_TICKS = 25;

    private static final float LOOK_SPEED = 0.05f;

    private float yaw, pitch;
    private int age, lockedTicks;
    private UUID rocketUuid = Util.NIL_UUID;

    private Vec3d lastAimOffset;
    private Boolean powered;
    private int opening = -1;
    private boolean closing;

    public final AnimationState activatingAnimation = new AnimationState();
    public final AnimationState deactivatingAnimation = new AnimationState();

    public RocketTurretBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public RocketTurretBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(PortalCubedBlocks.ROCKET_TURRET_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Age", age);
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
        nbt.putFloat("LockedTicks", lockedTicks);
        nbt.putUuid("RocketUUID", rocketUuid);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        age = nbt.getInt("Age");
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
        age++;
        if (powered == null) {
            powered = state.get(POWERED);
            if (!powered) {
                deactivatingAnimation.restart(age - 41);
            }
        } else if (powered != state.get(POWERED)) {
            powered = !powered;
            if (powered) {
                opening = 0;
                deactivatingAnimation.stop();
                activatingAnimation.restart(age);
                closing = false;
            } else {
                closing = true;
            }
            return;
        }
        if (opening >= 0 && opening < 60) {
            opening++;
            return;
        } else if (closing) {
            if (Math.abs(yaw) > 5 || Math.abs(pitch) > 5) {
                yaw = lerpDegrees(yaw, 0, 0.2f);
                pitch = lerpDegrees(pitch, 0, 0.2f);
            } else {
                yaw = 0;
                pitch = 0;
                activatingAnimation.stop();
                deactivatingAnimation.restart(age);
                closing = false;
            }
        }
        if (world.isClient || !powered) return;
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
        final BlockPos actualBody = getPos().up();
        final Vec3d eye = Vec3d.ofCenter(actualBody, 0.75);
        //noinspection DataFlowIssue
        final PlayerEntity player = world.getClosestPlayer(
            TargetPredicate.createNonAttackable().setPredicate(p -> p.world.raycast(new RaycastContext(
                // We can pass null here because of ShapeContextMixin
                eye, p.getPos().relative(Direction.UP, 0.75), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, null
            )).getType() == HitResult.Type.MISS),
            pos.getX(), pos.getY(), pos.getZ()
        );
        final Vec3d offset;
        if (player != null) {
            offset = player.getPos().subtract(Vec3d.ofBottomCenter(actualBody));
        } else if (lastAimOffset != null) {
            offset = lastAimOffset.withAxis(Direction.Axis.Y, 0);
        } else return;
        lastAimOffset = offset;
        final float newYaw = lerpDegrees(
            yaw, (float)Math.toDegrees(MathHelper.atan2(offset.z, offset.x)), LOOK_SPEED
        );
        final float newPitch = lerpDegrees(
            pitch, (float)Math.toDegrees(-MathHelper.atan2(offset.y, offset.x * offset.x + offset.z * offset.z)), LOOK_SPEED
        );
//        if (player != null && Math.max(Math.abs(newYaw - yaw), Math.abs(newPitch - pitch)) <= 5) {
//            lockedTicks++;
//            syncLockedTicks();
//            // Also play rocket_locking_beep1
//        }
        yaw = newYaw;
        pitch = newPitch;
        syncAngle();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }

    public int getAge() {
        return age;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    // https://stackoverflow.com/a/2708740/8840278
    private static float lerpDegrees(float start, float end, float amount) {
        final float difference = Math.abs(end - start);
        if (difference > 180) {
            if (end > start) {
                start += 360;
            } else {
                end += 360;
            }
        }
        return MathHelper.wrapDegrees(start + (end - start) * amount);
    }

    public enum State {
        SEARCHING,
        LOCKED,
        FIRING
    }
}
