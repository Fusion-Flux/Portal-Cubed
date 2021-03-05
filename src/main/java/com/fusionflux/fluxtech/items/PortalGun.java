package com.fusionflux.fluxtech.items;

import com.qouteall.immersive_portals.portal.Portal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class PortalGun extends Item {
    public int flipflop = 0;
    public int bigflipflop = 1;
    public BlockPos blockPos;
    public BlockPos blockPos2;
    public Portal portal;
    public Portal portal2;

    public PortalGun(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (flipflop == 1) {
            HitResult hitResult2 = user.raycast(128.0D, 0.0F, false);
            if (hitResult2.getType() == HitResult.Type.BLOCK) {
                blockPos2 = ((BlockHitResult) hitResult2).getBlockPos();
            }
        } else if (flipflop == 0) {
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) hitResult).getBlockPos();
            }
        }

        if (blockPos != null && blockPos2 != null) {

            if (portal != null && portal2 != null) {
                portal.kill();
                portal2.kill();
                System.out.println("removed portals");
            }

            portal = Portal.entityType.create(world);
            assert portal != null;
            portal.setOriginPos(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            portal.setDestinationDimension(World.OVERWORLD);
            portal.setDestination(new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
            portal.setOrientationAndSize(
                    new Vec3d(1, 0, 0), //axisW
                    new Vec3d(0, 1, 0), //axisH
                    4, 4
            );

            portal2 = Portal.entityType.create(world);
            assert portal2 != null;
            portal2.setOriginPos(new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
            portal2.setDestinationDimension(World.OVERWORLD);
            portal2.setDestination(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            portal2.setOrientationAndSize(
                    new Vec3d(1, 0, 0),//axisW
                    new Vec3d(0, 1, 0),//axisH
                    4,//width
                    4//height
            );
        }

        if (blockPos != null && blockPos2 != null) {
            portal.world.spawnEntity(portal);
            portal2.world.spawnEntity(portal2);
            System.out.println("made portals");
        }

        if (bigflipflop == 0) {
            if (flipflop == 0) {
                flipflop = 1;
            } else {
                flipflop = 0;
            }
            bigflipflop = 1;
        } else {
            bigflipflop = 0;
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}