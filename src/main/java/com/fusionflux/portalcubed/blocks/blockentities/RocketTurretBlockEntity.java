package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.GeneralUtil;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

import static com.fusionflux.portalcubed.blocks.RocketTurretBlock.POWERED;

public class RocketTurretBlockEntity extends EntityLikeBlockEntity {
	public static final int UPDATE_ANGLE = 0;
	public static final int UPDATE_LOCKED_TICKS = 1;

	public static final int LOCK_TICKS = 30;

	private static final Vec3 GUN_OFFSET = new Vec3(0.5, 1.71875, 0.09375);

	private int lockedTicks;
	private UUID rocketUuid = Util.NIL_UUID;

	private Vec2 destAngle;
	private Boolean powered;
	private int opening = -1;
	private boolean closing;
	public List<Tuple<Vec3, Vec3>> aimDests;

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
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putFloat("LockedTicks", lockedTicks);
		nbt.putUUID("RocketUUID", rocketUuid);
		nbt.putBoolean("Closing", closing);
		if (destAngle != null) {
			nbt.putIntArray("DestAngle", new int[] {Float.floatToRawIntBits(destAngle.x), Float.floatToRawIntBits(destAngle.y)});
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		lockedTicks = nbt.getInt("LockedTicks");
		rocketUuid = nbt.getUUID("RocketUUID");
		closing = nbt.getBoolean("Closing");
		final int[] destAngleA = nbt.getIntArray("DestAngle");
		if (destAngleA.length >= 2) {
			destAngle = new Vec2(
				Float.intBitsToFloat(destAngleA[0]),
				Float.intBitsToFloat(destAngleA[1])
			);
		}
	}

	public void setAngle(Tuple<Float, Float> angle) {
		yaw = angle.getA();
		pitch = angle.getB();
	}

	public void setLockedTicks(int lockedTicks) {
		final boolean wasFiring = getState() == State.FIRING;
		this.lockedTicks = lockedTicks;
		if (!wasFiring && getState() == State.FIRING) {
			shootAnimation.start(getAge());
		}
	}

	public void fire() {
		if (level == null || level.isClientSide) {
			PortalCubed.LOGGER.warn("RocketTurretBlockEntity.fire() should only be called on the server, not the client.");
			return;
		}
		final RocketEntity rocket = PortalCubedEntities.ROCKET.create(level);
		if (rocket != null) {
			rocketUuid = rocket.getUUID();
			rocket.setPos(Vec3.atLowerCornerOf(worldPosition).add(getGunOffset(0)));
			rocket.setYRot(yaw - 90);
			rocket.setXRot(pitch);
			level.addFreshEntity(rocket);
			level.playSound(null, rocket, PortalCubedSounds.ROCKET_FIRE_EVENT, SoundSource.HOSTILE, 1, 1);
		}
	}

	private void syncLockedTicks() {
		final FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(worldPosition);
		buf.writeByte(UPDATE_LOCKED_TICKS);
		buf.writeVarInt(lockedTicks);
		sendSyncPacket(buf);
	}

	private void syncAngle() {
		final FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(worldPosition);
		buf.writeByte(UPDATE_ANGLE);
		buf.writeFloat(yaw);
		buf.writeFloat(pitch);
		sendSyncPacket(buf);
	}

	private void sendSyncPacket(FriendlyByteBuf buf) {
		if (level == null || level.isClientSide) {
			PortalCubed.LOGGER.warn("Shouldn't have called sendSyncPacket from the client");
			return;
		}
		final ServerLevel serverWorld = (ServerLevel)level;
		final int viewDistance = serverWorld.getServer().getPlayerList().getViewDistance() * 16 + 1;
		for (final ServerPlayer player : serverWorld.players()) {
			if (player.blockPosition().closerThan(getBlockPos(), viewDistance)) {
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
	public void tick(Level world, BlockPos pos, BlockState state) {
		super.tick(world, pos, state);
		if (powered == null) {
			powered = state.getValue(POWERED);
			if (!powered) {
				deactivatingAnimation.start(getAge() - 41);
			}
		} else if (powered != state.getValue(POWERED)) {
			powered = !powered;
			if (powered) {
				opening = 0;
				deactivatingAnimation.stop();
				activatingAnimation.start(getAge());
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
				setYaw(Mth.rotLerp(0.05f, yaw, 0));
				setPitch(Mth.rotLerp(0.05f, pitch, 0));
			} else {
				yaw = 0;
				pitch = 0;
				activatingAnimation.stop();
				deactivatingAnimation.start(getAge());
				closing = false;
			}
		}
		if (!powered) {
			if (world.isClientSide) {
				aimDests = null;
			}
			setYaw(0);
			setPitch(0);
			return;
		}
		if (world.isClientSide) {
			final Vec3 gunPos = Vec3.atLowerCornerOf(getBlockPos()).add(getGunOffset(0));
			//noinspection DataFlowIssue
			aimDests = PortalDirectionUtils.raycast(world, new ClipContext(
				gunPos, gunPos.add(Vec3.directionFromRotation(pitch, yaw - 90).scale(127)),
				ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
				// We can pass null here because of CollisionContextMixin
				null
			)).rays().stream().map(r -> new Tuple<>(r.start(), r.end())).toList();
			return;
		}
		if (lockedTicks > 0) {
			if (lockedTicks++ == LOCK_TICKS) {
				fire();
				syncLockedTicks();
			} else {
				if (destAngle != null) {
					setYaw(Mth.rotLerp(0.05f, yaw, destAngle.y));
					setPitch(Mth.rotLerp(0.05f, pitch, destAngle.x));
					syncAngle();
				}
				if (lockedTicks == 9) {
					world.playSound(null, pos, PortalCubedSounds.ROCKET_LOCKED_EVENT, SoundSource.HOSTILE, 1f, 1f);
				} else if (
					lockedTicks > LOCK_TICKS &&
						world instanceof ServerLevel serverWorld &&
						(lockedTicks > LOCK_TICKS + 200 || serverWorld.getEntity(rocketUuid) == null)
				) {
					lockedTicks = 0;
					rocketUuid = Util.NIL_UUID;
					syncLockedTicks();
				}
			}
			return;
		}
		final BlockPos actualBody = getBlockPos().above();
		final Vec3 eye = Vec3.upFromBottomCenterOf(actualBody, GUN_OFFSET.y - 1);
		//noinspection DataFlowIssue
		final LivingEntity player = world.getNearestEntity(
			LivingEntity.class,
			TargetingConditions.forCombat().selector(e -> !(e instanceof CorePhysicsEntity) && e.level().clip(new ClipContext(
				eye, e.position().with(Direction.Axis.Y, e.getY(0.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null
			)).getType() == HitResult.Type.MISS),
			null,
			pos.getX(), pos.getY(), pos.getZ(),
			AABB.ofSize(eye, 128, 128, 128)
		);
		if (player != null) {
			final Vec3 offset = player.position()
				.with(Direction.Axis.Y, player.getY(0.5))
				.subtract(
					Vec3.atLowerCornerOf(pos)
						.add(getGunOffset(0))
				);
			destAngle = GeneralUtil.normalToRotation(offset);
			destAngle = new Vec2(destAngle.x, destAngle.y + 90);
		} else if (destAngle != null) {
			destAngle = new Vec2(0, destAngle.y);
		} else return;
		setYaw(Mth.rotLerp(0.05f, yaw, destAngle.y));
		setPitch(Mth.rotLerp(0.05f, pitch, destAngle.x));
		if (player != null && Math.abs(yaw - destAngle.y) <= 1 && Math.abs(pitch - destAngle.x) <= 1) {
			lockedTicks++;
			syncLockedTicks();
			world.playSound(null, pos, PortalCubedSounds.ROCKET_LOCKING_EVENT, SoundSource.HOSTILE, 1f, 1f);
		}
		syncAngle();
	}

	public Vec3 getGunOffset(float tickDelta) {
		return GUN_OFFSET
			.add(-0.3, -1.475, -0.5)
			.zRot((float)Math.toRadians(Mth.rotLerp(tickDelta, prevPitch, pitch)))
			.add(-0.2, -0.025, 0.0)
			.yRot((float)Math.toRadians(-Mth.rotLerp(tickDelta, prevYaw, yaw)))
			.add(0.5, 1.5, 0.5);
	}

	public enum State {
		SEARCHING,
		LOCKED,
		FIRING
	}
}
