package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class FaithPlateTargetBlock extends SimpleMultiSidedBlock {
    public FaithPlateTargetBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().creativeMode || !world.isClient) return ActionResult.PASS;
        final ItemStack held = player.getStackInHand(hand);
        if (!held.isOf(PortalCubedItems.HAMMER)) return ActionResult.PASS;

        final ItemStack result = new ItemStack(PortalCubedBlocks.CATAPULT.asItem());

        final Vec3d destPos = Vec3d.ofCenter(hit.getBlockPos())
            .add(Vec3d.of(hit.getSide().getVector()).multiply(-0.5));
        final NbtCompound catapultNbt = new NbtCompound();
        catapultNbt.putString("OptionsListData", String.format(
            "{destX:%1$f,destY:%2$f,destZ:%3$f,angle:45}", // It's Gson lenient, so we don't need quotes
            destPos.x, destPos.y, destPos.z
        ));
        BlockItem.writeBlockEntityNbtToStack(result, PortalCubedBlocks.CATAPULT_BLOCK_ENTITY, catapultNbt);

        final NbtCompound display = new NbtCompound();
        final NbtList lore = new NbtList();
        lore.add(NbtString.of("\"(+NBT)\""));
        display.put("Lore", lore);
        result.setSubNbt("display", display);

        final PlayerInventory inventory = player.getInventory();
        inventory.addPickBlock(result);
        setPickBlock();

        return ActionResult.SUCCESS;
    }

    @ClientOnly
    private void setPickBlock() {
        final MinecraftClient client = MinecraftClient.getInstance();
        assert client.interactionManager != null;
        assert client.player != null;
        client.interactionManager.clickCreativeStack(
            client.player.getStackInHand(Hand.MAIN_HAND),
            36 + client.player.getInventory().selectedSlot
        );
    }
}
