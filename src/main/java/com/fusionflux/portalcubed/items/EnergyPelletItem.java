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
        final EnergyPelletEntity entity = PortalCubedEntities.ENERGY_PELLET.create(world);
        if (entity == null) return TypedActionResult.pass(item);
        entity.setPosition(user.getEyePos().add(user.getRotationVector()));
        entity.setYaw(user.getYaw());
        entity.setPitch(user.getPitch());
        world.spawnEntity(entity);
        return TypedActionResult.consume(item);
    }
}
