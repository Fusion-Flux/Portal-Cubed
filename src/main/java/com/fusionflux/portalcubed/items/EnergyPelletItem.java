package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnergyPelletItem extends Item {
    public EnergyPelletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack item = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(item);
        if (!user.getAbilities().creativeMode) {
            item.decrement(1);
        }
        final EnergyPelletEntity pellet = PortalCubedEntities.ENERGY_PELLET.create(world);
        if (pellet == null) return TypedActionResult.pass(item);
        pellet.setPosition(user.getEyePos().add(user.getRotationVector()));
        pellet.setVelocity(user.getVelocity().add(user.getRotationVector().multiply(0.25)));
        if (user.isSneaking()) {
            pellet.resetLife(-1);
        }
        world.spawnEntity(pellet);
        return TypedActionResult.consume(item);
    }
}