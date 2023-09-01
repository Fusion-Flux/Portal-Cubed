package com.fusionflux.portalcubed.gui;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BlockPosScreenHandler extends AbstractContainerMenu {
	private final BlockPos at;

	public BlockPosScreenHandler(MenuType<BlockPosScreenHandler> type, int syncId, BlockPos at) {
		super(type, syncId);
		this.at = at;
	}

	public static ExtendedScreenHandlerType<BlockPosScreenHandler> createType() {
		@SuppressWarnings("unchecked")
		final ExtendedScreenHandlerType<BlockPosScreenHandler>[] type = new ExtendedScreenHandlerType[1];
		return type[0] = new ExtendedScreenHandlerType<>(
			(syncId, inventory, buf) -> new BlockPosScreenHandler(type[0], syncId, buf.readBlockPos())
		);
	}

	public static MenuType<BlockPosScreenHandler> registerNew(String id) {
		return Registry.register(BuiltInRegistries.MENU, id(id), createType());
	}

	@NotNull
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
