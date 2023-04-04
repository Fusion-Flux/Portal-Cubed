package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

import static com.fusionflux.portalcubed.blocks.RocketTurretBlock.POWERED;

public class RocketTurretBlockEntity extends EntityLikeBlockEntity {
    public static final int UPDATE_ANGLE = 0;
    public static final int UPDATE_LOCKED_TICKS = 1;

    public static final int LOCK_TICKS = 30;

    private static final Vec3d GUN_OFFSET = new Vec3d(0.5, 1.71875, 0.09375);

    private int lockedTicks;
    private UUID rocketUuid = Util.NIL_UUID;

    private Vec2f destAngle;
    private Boolean powered;
    private int opening = -1;
    private boolean closing;
    public List<Pair<Vec3d, Vec3d>> aimDests;

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
        super.writeNbt(nbt);
        nbt.putFloat("LockedTicks", lockedTicks);
        nbt.putUuid("RocketUUID", rocketUuid);
        nbt.putBoolean("Closing", closing);
        if (destAngle != null) {
            nbt.putIntArray("DestAngle", new int[] {Float.floatToRawIntBits(destAngle.x), Float.floatToRawIntBits(destAngle.y)});
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        lockedTicks = nbt.getInt("LockedTicks");
        rocketUuid = nbt.getUuid("RocketUUID");
        closing = nbt.getBoolean("Closing");
        final int[] destAngleA = nbt.getIntArray("DestAngle");
        if (destAngleA.length >= 2) {
            destAngle = new Vec2f(
                Float.intBitsToFloat(destAngleA[0]),
                Float.intBitsToFloat(destAngleA[1])
            );
        }
    }

    public void setAngle(Pair<Float, Float> angle) {
        yaw = angle.getLeft();
        pitch = angle.getRight();
    }

    public void setLockedTicks(int lockedTicks) {
        final boolean wasFiring = getState() == State.FIRING;
        this.lockedTicks = lockedTicks;
        if (!wasFiring && getState() == State.FIRING) {
            shootAnimation.restart(getAge());
        }
    }

    public void fire() {
        if (world == null || world.isClient) {
            PortalCubed.LOGGER.warn("RocketTurretBlockEntity.fire() should only be called on the server, not the client.");
            return;
        }
        final RocketEntity rocket = PortalCubedEntities.ROCKET.create(world);
        if (rocket != null) {
            rocketUuid = rocket.getUuid();
            rocket.setPosition(Vec3d.of(pos).add(getGunOffset(0)));
            rocket.setYaw(yaw - 90);
            rocket.setPitch(pitch);
            world.spawnEntity(rocket);
            world.playSoundFromEntity(null, rocket, PortalCubedSounds.ROCKET_FIRE_EVENT, SoundCategory.HOSTILE, 1, 1);
        }
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

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);
        if (powered == null) {
            powered = state.get(POWERED);
            if (!powered) {
                deactivatingAnimation.restart(getAge() - 41);
            }
        } else if (powered != state.get(POWERED)) {
            powered = !powered;
            if (powered) {
                opening = 0;
                deactivatingAnimation.stop();
                activatingAnimation.restart(getAge());
                closing = false;
            } else {
                lockedTicks = 0;
                closing = true;
            }
            return;
        }
        if (opening >= 0 && opening < 80) {
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
                deactivatingAnimation.restart(getAge());
                closing = false;
            }
        }
        if (!powered) {
            if (world.isClient) {
                aimDests = null;
            }
            setYaw(0);
            setPitch(0);
            return;
        }
        if (world.isClient) {
            final Vec3d gunPos = Vec3d.of(getPos()).add(getGunOffset(0));
            //noinspection DataFlowIssue
            aimDests = PortalDirectionUtils.raycast(world, new RaycastContext(
                gunPos, gunPos.add(Vec3d.fromPolar(pitch, yaw - 90).multiply(127)),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE,
                // We can pass null here because of ShapeContextMixin
                null
            )).rays().stream().map(r -> new Pair<>(r.start(), r.end())).toList();
            return;
        }
        if (lockedTicks > 0) {
            if (lockedTicks++ == LOCK_TICKS) {
                fire();
                syncLockedTicks();
            } else {
                if (destAngle != null) {
                    setYaw(MathHelper.lerpAngleDegrees(0.05f, yaw, destAngle.y));
                    setPitch(MathHelper.lerpAngleDegrees(0.05f, pitch, destAngle.x));
                    syncAngle();
                }
                if (lockedTicks == 9) {
                    world.playSound(null, pos, PortalCubedSounds.ROCKET_LOCKED_EVENT, SoundCategory.HOSTILE, 1f, 1f);
                } else if (
                    lockedTicks > LOCK_TICKS &&
                        world instanceof ServerWorld serverWorld &&
                        (lockedTicks > LOCK_TICKS + 200 || serverWorld.getEntity(rocketUuid) == null)
                ) {
                    lockedTicks = 0;
                    rocketUuid = Util.NIL_UUID;
                    syncLockedTicks();
                }
            }
            return;
        }
        final BlockPos actualBody = getPos().up();
        final Vec3d eye = Vec3d.ofCenter(actualBody, GUN_OFFSET.y - 1);
        //noinspection DataFlowIssue
        final LivingEntity player = world.getClosestEntity(
            LivingEntity.class,
            TargetPredicate.createAttackable().setPredicate(e -> !(e instanceof CorePhysicsEntity) && e.world.raycast(new RaycastContext(
                eye, e.getPos().withAxis(Direction.Axis.Y, e.getBodyY(0.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, null
            )).getType() == HitResult.Type.MISS),
            null,
            pos.getX(), pos.getY(), pos.getZ(),
            Box.of(eye, 128, 128, 128)
        );
        if (player != null) {
            final Vec3d offset = player.getPos()
                .withAxis(Direction.Axis.Y, player.getBodyY(0.5))
                .subtract(
                    Vec3d.of(pos)
                        .add(getGunOffset(0))
                );
            destAngle = new Vec2f(
                (float)Math.toDegrees(-MathHelper.atan2(offset.y, Math.sqrt(offset.x * offset.x + offset.z * offset.z))),
                (float)Math.toDegrees(MathHelper.atan2(offset.z, offset.x))
            );
        } else if (destAngle != null) {
            destAngle = new Vec2f(0, destAngle.y);
        } else return;
        setYaw(MathHelper.lerpAngleDegrees(0.05f, yaw, destAngle.y));
        setPitch(MathHelper.lerpAngleDegrees(0.05f, pitch, destAngle.x));
        if (player != null && Math.abs(yaw - destAngle.y) <= 1 && Math.abs(pitch - destAngle.x) <= 1) {
            lockedTicks++;
            syncLockedTicks();
            world.playSound(null, pos, PortalCubedSounds.ROCKET_LOCKING_EVENT, SoundCategory.HOSTILE, 1f, 1f);
        }
        syncAngle();
    }

    public Vec3d getGunOffset(float tickDelta) {
        return GUN_OFFSET
            .add(-0.3, -1.475, -0.5)
            .rotateZ((float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, prevPitch, pitch)))
            .add(-0.2, -0.025, 0.0)
            .rotateY((float)Math.toRadians(-MathHelper.lerpAngleDegrees(tickDelta, prevYaw, yaw)))
            .add(0.5, 1.5, 0.5);
    }

    public enum State {
        SEARCHING,
        LOCKED,
        FIRING
    }
}
