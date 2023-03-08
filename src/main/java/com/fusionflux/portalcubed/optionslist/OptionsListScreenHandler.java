package com.fusionflux.portalcubed.optionslist;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class OptionsListScreenHandler extends ScreenHandler {
    private final BlockPos at;

    public OptionsListScreenHandler(int syncId, @SuppressWarnings("unused") PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, buf.readBlockPos());
    }

    public OptionsListScreenHandler(int syncId, BlockPos at) {
        super(PortalCubed.OPTIONS_LIST_SCREEN_HANDLER, syncId);
        this.at = at;
    }

    @Override
    public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.isCreative();
    }

    public BlockPos getAt() {
        return at;
    }
}
