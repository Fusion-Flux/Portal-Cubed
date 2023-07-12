package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CatapultBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private double destX;
    private double destY;
    private double destZ;
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
    public void load(CompoundTag nbt) {
        destX = nbt.getDouble("DestX");
        destY = nbt.getDouble("DestY");
        destZ = nbt.getDouble("DestZ");
        angle = nbt.contains("Angle") ? nbt.getDouble("Angle") : 45;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putDouble("DestX", destX);
        nbt.putDouble("DestY", destY);
        nbt.putDouble("DestZ", destZ);
        nbt.putDouble("Angle", angle);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return null; // TODO: Implement GUI
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    private double getRelX(double startX) {
        return destX - startX - 0.5;
    }

    private double getRelZ(double startZ) {
        return destZ - startZ - 0.5;
    }

    public double getRelH(double startX, double startZ) {
        return Math.sqrt(Mth.square(getRelX(startX)) + Mth.square(getRelZ(startZ)));
    }

    public double getRelY(double startY) {
        return destY - startY;
    }

    public double getAngle() {
        return angle;
    }

    public Vec3 getLaunchDir(double startX, double startZ) {
        final double a = Math.toRadians(angle);
        return new Vec3(getRelX(startX), 0, getRelZ(startZ))
            .normalize()
            .scale(Math.cos(a))
            .add(0, Math.sin(a), 0);
    }
}
