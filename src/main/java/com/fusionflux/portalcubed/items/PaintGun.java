package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.GelOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PaintGun extends Item {

    public PaintGun(Settings settings) {
        super(settings);
    }

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateNbt().putBoolean("complementary", false);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateNbt().putBoolean("complementary", true);

        if (!world.isClient) {
            throwGel(world, user);
        }
        return TypedActionResult.pass(stack);
    }

    protected void throwGel(World world, LivingEntity player) {
        GelOrbEntity gelOrbEntity = new GelOrbEntity(world, player);
        gelOrbEntity.setItem(new ItemStack(PortalCubedItems.GEL_ORB));
        gelOrbEntity.setProperties(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 0F);
        world.spawnEntity(gelOrbEntity); // spawns entity
    }
}