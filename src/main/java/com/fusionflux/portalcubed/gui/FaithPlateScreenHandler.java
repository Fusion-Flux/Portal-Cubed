package com.fusionflux.portalcubed.gui;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class FaithPlateScreenHandler extends ScreenHandler {
    private BlockPos pos;
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public FaithPlateScreenHandler(int syncId, @SuppressWarnings("unused") PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId);
        this.pos = buf.readBlockPos();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public BlockPos getPos() {
        return pos;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public FaithPlateScreenHandler(int syncId) {
        super(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, syncId);
    }

    @Override
    public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
