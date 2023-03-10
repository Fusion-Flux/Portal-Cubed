package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.optionslist.OptionsListBlockEntity;
import com.fusionflux.portalcubed.optionslist.OptionsListData;
import com.fusionflux.portalcubed.optionslist.OptionsListScreenHandler;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CatapultBlockEntity extends OptionsListBlockEntity implements ExtendedScreenHandlerFactory {
    @MidnightConfig.Entry(min = -30_000_000, max = 30_000_000)
    private double destX;

    @MidnightConfig.Entry(min = -2032, max = 4064)
    private double destY;

    @MidnightConfig.Entry(min = -30_000_000, max = 30_000_000)
    private double destZ;

    @MidnightConfig.Entry(min = 0, max = 90)
    private double angle = 45;

    public CatapultBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        destX = pos.getX() + 0.5;
        destY = pos.getY();
        destZ = pos.getZ() + 0.5;
    }

    public CatapultBlockEntity(BlockPos pos, BlockState state) {
        this(PortalCubedBlocks.CATAPULT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        OptionsListData.read(nbt, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        OptionsListData.write(nbt, this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new OptionsListScreenHandler(i, pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    private double getRelX(double startX) {
        return destX - startX - 0.5;
    }

    private double getRelZ(double startZ) {
        return destZ - startZ - 0.5;
    }

    public double getRelH(double startX, double startZ) {
        return Math.sqrt(MathHelper.square(getRelX(startX)) + MathHelper.square(getRelZ(startZ)));
    }

    public double getRelY(double startY) {
        return destY - startY;
    }

    public double getAngle() {
        return angle;
    }

    public Vec3d getLaunchDir(double startX, double startZ) {
        final double a = Math.toRadians(angle);
        return new Vec3d(getRelX(startX), 0, getRelZ(startZ))
            .normalize()
            .multiply(Math.cos(a))
            .add(0, Math.sin(a), 0);
    }
}
