package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class BetaFaithPlateBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private double velX = 0;
    private double velY = 0;
    private double velZ = 0;

    private  double timer = 0;
    private  double animationTimer = 0;

    public BetaFaithPlateBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.BETA_FAITH_PLATE_ENTITY, pos, state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, BetaFaithPlateBlockEntity blockEntity) {
        Box checkBox = new Box(pos).offset(state.get(Properties.FACING).getOffsetX(), state.get(Properties.FACING).getOffsetY(), state.get(Properties.FACING).getOffsetZ());

        List<Entity> list = world.getNonSpectatingEntities(Entity.class, checkBox);

        if (blockEntity.animationTimer > 0) {
            blockEntity.animationTimer -= 1;
            world.setBlockState(pos, state.with(Properties.ENABLED, true), 3);
        } else {
            world.setBlockState(pos, state.with(Properties.ENABLED, false), 3);
        }

        final boolean launch = new Vec3d(blockEntity.velX, blockEntity.velY, blockEntity.velZ).lengthSquared() > 1e-7;
        for (Entity liver : list) {
            if (blockEntity.timer <= 0) {
                if (liver instanceof CorePhysicsEntity physEn && physEn.getHolderUUID().isPresent()) {
                    continue;
                }
                if (launch) {
                    liver.setVelocity(blockEntity.velX, blockEntity.velY, blockEntity.velZ);
                }
                blockEntity.timer = 5;
                blockEntity.animationTimer = 50;
                world.setBlockState(pos, state.with(Properties.ENABLED, false), 3);
                world.playSound(
                    null,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    PortalCubedSounds.CATAPULT_LAUNCH_EVENT, SoundCategory.BLOCKS,
                    1f, 1f
                );
            }
        }
        if (blockEntity.timer > 0)
            blockEntity.timer -= 1;

    }


    public void setVelX(double velX) {
        this.velX = velX;
    }
    public void setVelY(double velY) {
        this.velY = velY;
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
        return null;
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
}
