package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
        pellet.setPosition(user.getCameraPosVec(0).add(user.getRotationVector()));
        Vec3d userVelocity = user.getVelocity();
        if (user.isOnGround()) {
            userVelocity = userVelocity.withAxis(Direction.Axis.Y, 0);
        }
        pellet.setVelocity(userVelocity.add(user.getRotationVector().multiply(0.25)));
        if (user.isSneaking()) {
            pellet.resetLife(-1);
        }
        world.spawnEntity(pellet);
        return TypedActionResult.consume(item);
    }
}
