package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FaithPlateScreenHandler extends ScreenHandler {
    private Vec3d vel;
    private BlockPos pos;
    private double x =0;
    private double y =0;
    private double z =0;

    public FaithPlateScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory);
        this.pos = buf.readBlockPos();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        //vel = new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
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

    public FaithPlateScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(PortalCubed.FAITH_PLATE_SCREEN_HANDLER, syncId);
        //int m;
        //int l;
        ////The player inventory
        //for (m = 0; m < 3; ++m) {
        //    for (l = 0; l < 9; ++l) {
        //        this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
        //    }
        //}
        ////The player Hotbar
        //for (m = 0; m < 9; ++m) {
        //    this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        //}
//
        ////vel = Vec3d.ZERO;
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
