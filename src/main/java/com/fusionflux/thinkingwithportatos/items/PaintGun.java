package com.fusionflux.thinkingwithportatos.items;

import com.fusionflux.thinkingwithportatos.entity.GelOrbEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PaintGun extends Item {
    protected LivingEntity player;
    protected boolean using = false;
    protected boolean usingLeft = false;

    public PaintGun(Settings settings) {
        super(settings);
    }

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", false);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", true);
        usingLeft = false;
        using = true;
        player = user;
        return TypedActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        using = false;
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && using) {
            throwGel(world);
        }
    }

    protected void throwGel(World world) {
        GelOrbEntity gelOrbEntity = new GelOrbEntity(world, player);
        gelOrbEntity.setItem(new ItemStack(ThinkingWithPortatosItems.GEL_ORB));
        gelOrbEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 0F);
        world.spawnEntity(gelOrbEntity); // spawns entity
    }
}