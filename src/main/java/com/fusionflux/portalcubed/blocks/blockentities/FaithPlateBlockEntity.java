package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.FaithPlateBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.listeners.ServerAnimatable;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);
        if (state.get(FaithPlateBlock.FACING).getAxis().isVertical()) {
            final boolean isFlipped = state.get(FaithPlateBlock.FACING) == Direction.DOWN;
            setYaw(directionToAngle(state.get(FaithPlateBlock.HORIFACING)) * (isFlipped ? -1 : 1));
            setPitch(isFlipped ? 180f : 0f);
        } else {
            setYaw(directionToAngle(state.get(FaithPlateBlock.FACING)) + 180f);
            setPitch(90f);
        }

        Box checkBox = new Box(pos).offset(
            state.get(FaithPlateBlock.FACING).getOffsetX(),
            state.get(FaithPlateBlock.FACING).getOffsetY(),
            state.get(FaithPlateBlock.FACING).getOffsetZ()
        );

        List<Entity> list = world.getNonSpectatingEntities(Entity.class, checkBox);

        final boolean launch = new Vec3d(velX, velY, velZ).lengthSquared() > 1e-7;
        for (Entity liver : list) {
            if (timer <= 0) {
                if (liver instanceof CorePhysicsEntity physEn && physEn.getHolderUUID().isPresent()) {
                    continue;
                }
                if (launch) {
                    RayonIntegration.INSTANCE.setVelocity(liver, new Vec3d(velX, velY, velZ));
                }
                timer = 5;
                if (!world.isClient) {
                    final PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    buf.writeString("fling");
                    //noinspection DataFlowIssue
                    world.getServer()
                        .getPlayerManager()
                        .sendToAround(
                            null,
                            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                            96, world.getRegistryKey(),
                            ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.SERVER_ANIMATE, buf)
                        );
                }
                world.playSound(
                    null,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    PortalCubedSounds.CATAPULT_LAUNCH_EVENT, SoundCategory.BLOCKS,
                    1f, 1f
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
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putDouble("velX", velX);
        tag.putDouble("velY", velY);
        tag.putDouble("velZ", velZ);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        velX = tag.getDouble("velX");
        velY = tag.getDouble("velY");
        velZ = tag.getDouble("velZ");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeDouble(velX);
        buf.writeDouble(velY);
        buf.writeDouble(velZ);
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FaithPlateScreenHandler(i);
    }

    public void updateListeners() {
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    @Nullable
    public AnimationState getAnimation(String name) {
        return name.equals("fling") ? flingState : null;
    }
}
