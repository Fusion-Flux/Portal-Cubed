package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.advancements.triggers.PortalCubedTriggers;
import com.fusionflux.portalcubed.blocks.FaithPlateBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.listeners.ServerAnimatable;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;

public class FaithPlateBlockEntity extends EntityLikeBlockEntity implements ExtendedScreenHandlerFactory, ServerAnimatable {
	private double velX = 0;
	private double velY = 0;
	private double velZ = 0;

	private double timer = 0;
	public final AnimationState flingState = new AnimationState();

	public FaithPlateBlockEntity(BlockEntityType<? extends FaithPlateBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public FaithPlateBlockEntity(BlockPos pos, BlockState state) {
		this(PortalCubedBlocks.FAITH_PLATE_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void tick(Level world, BlockPos pos, BlockState state) {
		super.tick(world, pos, state);
		if (state.getValue(FaithPlateBlock.FACING).getAxis().isVertical()) {
			final boolean isFlipped = state.getValue(FaithPlateBlock.FACING) == Direction.DOWN;
			setYaw(directionToAngle(state.getValue(FaithPlateBlock.HORIFACING)) * (isFlipped ? -1 : 1));
			setPitch(isFlipped ? 180f : 0f);
		} else {
			setYaw(directionToAngle(state.getValue(FaithPlateBlock.FACING)) + 180f);
			setPitch(90f);
		}

		AABB checkBox = new AABB(pos).move(
			state.getValue(FaithPlateBlock.FACING).getStepX(),
			state.getValue(FaithPlateBlock.FACING).getStepY(),
			state.getValue(FaithPlateBlock.FACING).getStepZ()
		);

		List<Entity> list = world.getEntitiesOfClass(Entity.class, checkBox);

		final boolean launch = new Vec3(velX, velY, velZ).lengthSqr() > 1e-7;
		for (Entity liver : list) {
			if (timer <= 0) {
				if (liver instanceof CorePhysicsEntity physEn && physEn.getHolderUUID().isPresent()) {
					continue;
				}
				if (launch) {
					final Vec3 force = new Vec3(velX, velY, velZ);
					RayonIntegration.INSTANCE.setVelocity(liver, force);
					if (liver instanceof ServerPlayer player) {
						PortalCubedTriggers.FLING.trigger(player, worldPosition, force);
					}
				}
				timer = 5;
				if (!world.isClientSide) {
					final FriendlyByteBuf buf = PacketByteBufs.create();
					buf.writeBlockPos(pos);
					buf.writeUtf("fling");
					//noinspection DataFlowIssue
					world.getServer()
						.getPlayerList()
						.broadcast(
							null,
							pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
							96, world.dimension(),
							ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.SERVER_ANIMATE, buf)
						);
				}
				world.playSound(
					null,
					pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
					PortalCubedSounds.CATAPULT_LAUNCH_EVENT, SoundSource.BLOCKS,
						.2f, 1f
				);
			}
		}
		if (timer > 0) {
			timer--;
		}
	}

	private static float directionToAngle(Direction dir) {
		return switch (dir) {
			case EAST -> 90f;
			case SOUTH -> 180f;
			case WEST -> -90f;
			case NORTH -> 0f;
			default -> throw new AssertionError("FaithPlateBlockEntity.directionToAngle called with non-horizontal direction " + dir);
		};
	}

	public double getVelX() {
		return velX;
	}

	public void setVelX(double velX) {
		this.velX = velX;
	}

	public double getVelY() {
		return velY;
	}

	public void setVelY(double velY) {
		this.velY = velY;
	}

	public double getVelZ() {
		return velZ;
	}

	public void setVelZ(double velZ) {
		this.velZ = velZ;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putDouble("velX", velX);
		tag.putDouble("velY", velY);
		tag.putDouble("velZ", velZ);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		velX = tag.getDouble("velX");
		velY = tag.getDouble("velY");
		velZ = tag.getDouble("velZ");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(this.worldPosition);
		buf.writeDouble(velX);
		buf.writeDouble(velY);
		buf.writeDouble(velZ);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
		return new FaithPlateScreenHandler(i);
	}

	public void updateListeners() {
		setChanged();
		assert level != null;
		level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	@Nullable
	public AnimationState getAnimation(String name) {
		return name.equals("fling") ? flingState : null;
	}
}
