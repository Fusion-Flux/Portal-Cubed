package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class FaithPlateTargetBlock extends SimpleMultiSidedBlock {
	public FaithPlateTargetBlock(Properties settings) {
		super(settings);
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!player.getAbilities().instabuild || !world.isClientSide) return InteractionResult.PASS;
		final ItemStack held = player.getItemInHand(hand);
		if (!held.is(PortalCubedItems.WRENCHES)) return InteractionResult.PASS;

		final ItemStack result = new ItemStack(PortalCubedBlocks.CATAPULT.asItem());

		final Vec3 destPos = Vec3.atCenterOf(hit.getBlockPos())
			.add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(-0.5));
		final CompoundTag catapultNbt = new CompoundTag();
		catapultNbt.putDouble("DestX", destPos.x);
		catapultNbt.putDouble("DestY", destPos.y);
		catapultNbt.putDouble("DestZ", destPos.z);
		BlockItem.setBlockEntityData(result, PortalCubedBlocks.CATAPULT_BLOCK_ENTITY, catapultNbt);

		final CompoundTag display = new CompoundTag();
		final ListTag lore = new ListTag();
		lore.add(StringTag.valueOf("\"(+NBT)\""));
		display.put("Lore", lore);
		result.addTagElement("display", display);

		final Inventory inventory = player.getInventory();
		inventory.setPickedItem(result);
		setPickBlock();

		return InteractionResult.SUCCESS;
	}

	@ClientOnly
	private void setPickBlock() {
		final Minecraft client = Minecraft.getInstance();
		assert client.gameMode != null;
		assert client.player != null;
		client.gameMode.handleCreativeModeItemAdd(
			client.player.getItemInHand(InteractionHand.MAIN_HAND),
			36 + client.player.getInventory().selected
		);
	}
}
