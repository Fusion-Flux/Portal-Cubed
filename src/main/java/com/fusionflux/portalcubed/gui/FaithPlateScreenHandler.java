package com.fusionflux.portalcubed.gui;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FaithPlateScreenHandler extends AbstractContainerMenu {
    private BlockPos pos;
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public FaithPlateScreenHandler(int syncId, @SuppressWarnings("unused") Inventory playerInventory, FriendlyByteBuf buf) {
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
    public ItemStack quickMoveStack(Player player, int fromIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
