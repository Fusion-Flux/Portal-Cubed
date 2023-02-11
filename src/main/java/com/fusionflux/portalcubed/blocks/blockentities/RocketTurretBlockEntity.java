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

    public static final int LOCK_TICKS = 30;

    private static final Vec3d GUN_OFFSET = new Vec3d(0.5, 1.71875, 0.09375);

    private float yaw, pitch;
    private int age, lockedTicks;
    private UUID rocketUuid = Util.NIL_UUID;

    private Vec3d lastAimOffset;
    private Boolean powered;
    private int opening = -1;
    private boolean closing;
    public float lastYaw, lastPitch;
    public Vec3d aimDest;

    public final AnimationState activatingAnimation = new AnimationState();
    public final AnimationState deactivatingAnimation = new AnimationState();
    public final AnimationState shootAnimation = new AnimationState();

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
        if (world == null || world.isClient) {
            PortalCubed.LOGGER.warn("RocketTurretBlockEntity.fire() should only be called on the server, not the client.");
            return;
        }
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
        lastYaw = yaw;
        lastPitch = pitch;
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
                setYaw(MathHelper.lerpAngleDegrees(0.05f, yaw, 0));
                setPitch(MathHelper.lerpAngleDegrees(0.05f, pitch, 0));
            } else {
                yaw = 0;
                pitch = 0;
                activatingAnimation.stop();
                deactivatingAnimation.restart(age);
                closing = false;
            }
        }
        if (!powered) {
            if (world.isClient) {
                aimDest = null;
            }
            return;
        }
        if (world.isClient) {
            final Vec3d gunPos = Vec3d.of(getPos()).add(getGunOffset(0));
            //noinspection DataFlowIssue
            aimDest = world.raycast(new RaycastContext(
                gunPos, gunPos.add(Vec3d.fromPolar(pitch, yaw - 90).multiply(127)),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE,
                // We can pass null here because of ShapeContextMixin
                null
            )).getPos();
            if (lockedTicks == 1) {
                shootAnimation.restart(age);
            }
            return;
        }
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
        final Vec3d eye = Vec3d.ofCenter(actualBody, GUN_OFFSET.y - 1);
        //noinspection DataFlowIssue
        final PlayerEntity player = world.getClosestPlayer(
            TargetPredicate.createNonAttackable().setPredicate(p -> p.world.raycast(new RaycastContext(
                eye, p.getPos().withAxis(Direction.Axis.Y, p.getBodyY(0.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, null
            )).getType() == HitResult.Type.MISS),
            pos.getX(), pos.getY(), pos.getZ()
        );
        final Vec3d offset;
        if (player != null) {
            offset = player.getPos()
                .withAxis(Direction.Axis.Y, player.getBodyY(0.5))
                .subtract(
                    Vec3d.of(pos)
                        .add(getGunOffset(0))
                );
        } else if (lastAimOffset != null) {
            offset = lastAimOffset.withAxis(Direction.Axis.Y, 0);
        } else return;
        lastAimOffset = offset;
        final float destYaw = (float)Math.toDegrees(MathHelper.atan2(offset.z, offset.x));
        setYaw(MathHelper.lerpAngleDegrees(0.05f, yaw, destYaw));
        setPitch(MathHelper.lerpAngleDegrees(
            0.05f, pitch, (float)Math.toDegrees(-MathHelper.atan2(offset.y, Math.abs(offset.x) + Math.abs(offset.z)))
        ));
        if (player != null && Math.abs(yaw - destYaw) <= 5) {
            lockedTicks++;
            syncLockedTicks();
            // Also play rocket_locked_beep1
        }
        syncAngle();
    }

    public Vec3d getGunOffset(float tickDelta) {
        return GUN_OFFSET
            .add(-0.3, -1.475, -0.5)
            .rotateZ((float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, lastPitch, pitch)))
            .add(-0.2, -0.025, 0.0)
            .rotateY((float)Math.toRadians(-MathHelper.lerpAngleDegrees(tickDelta, lastYaw, yaw)))
            .add(0.5, 1.5, 0.5);
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

    public void setYaw(float yaw) {
        this.yaw = MathHelper.wrapDegrees(yaw);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = MathHelper.wrapDegrees(pitch);
    }

    public enum State {
        SEARCHING,
        LOCKED,
        FIRING
    }
}
