package com.fusionflux.portalcubed.optionslist;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class OptionsListScreenHandler extends AbstractContainerMenu {
    private final BlockPos at;

    public OptionsListScreenHandler(int syncId, @SuppressWarnings("unused") Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, buf.readBlockPos());
    }

    public OptionsListScreenHandler(int syncId, BlockPos at) {
        super(PortalCubed.OPTIONS_LIST_SCREEN_HANDLER, syncId);
        this.at = at;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int fromIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isCreative();
    }

    public BlockPos getAt() {
        return at;
    }
}
