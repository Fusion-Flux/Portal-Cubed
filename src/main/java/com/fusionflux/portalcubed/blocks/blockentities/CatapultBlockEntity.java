package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.optionslist.OptionsListBlockEntity;
import com.fusionflux.portalcubed.optionslist.OptionsListData;
import com.fusionflux.portalcubed.optionslist.OptionsListScreenHandler;
import eu.midnightdust.lib.config.MidnightConfig;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
    public void load(CompoundTag nbt) {
        OptionsListData.read(nbt, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        OptionsListData.write(nbt, this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new OptionsListScreenHandler(i, worldPosition);
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
