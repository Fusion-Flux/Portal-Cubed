package com.fusionflux.fluxtech.items;

import com.qouteall.immersive_portals.portal.Portal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class PortalGun extends Item {

    public PortalGun(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Portal portal = Portal.entityType.create(world);
        portal.setOriginPos(new Vec3d(0, 70, 0));
        portal.setDestinationDimension(World.NETHER);
        portal.setDestination(new Vec3d(100, 70, 100));
        portal.setOrientationAndSize(
                new Vec3d(1, 0, 0),//axisW
                new Vec3d(0, 1, 0),//axisH
                4,//width
                4//height
        );
        portal.world.spawnEntity(portal);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}