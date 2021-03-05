package com.fusionflux.fluxtech.items;


import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.portal.GeometryPortalShape;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.portal.PortalManipulation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class PortalGun extends Item {
    public int flipflop = 0;
    public int bigflipflop = 1;
    public BlockPos blockPos;
    public BlockPos blockPos2;
    public static Portal portal;
    public static Portal portal2;
    public Vec3d direction1;
    public Vec3d direction2;
    public Quaternion quion1;
    public Quaternion quion2;


    public PortalGun(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (flipflop == 1) {
            HitResult hitResult2 = user.raycast(128.0D, 0.0F, false);
            if (hitResult2.getType() == HitResult.Type.BLOCK) {
                blockPos2 = ((BlockHitResult) hitResult2).getBlockPos();
                direction2 = new Vec3d(((BlockHitResult) hitResult2).getSide().getVector().getX(), ((BlockHitResult) hitResult2).getSide().getVector().getY(), ((BlockHitResult) hitResult2).getSide().getVector().getZ());
                quion1 = new Quaternion(((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion());            }
        } else if (flipflop == 0) {
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) hitResult).getBlockPos();
                direction1 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                quion2 = new Quaternion(((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion());

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
            portal.setOriginPos(new Vec3d(blockPos.getX() + .5 + (direction1.x / 1.99), blockPos.getY()+ (direction1.y*1.01), blockPos.getZ() + .5 + (direction1.z / 1.99)-(direction2.y/2)));
            portal.setDestinationDimension(World.OVERWORLD);
            portal.setOrientationAndSize(
                    new Vec3d(direction1.z+direction1.y, 0, -(direction1.x)),//axisW
                    new Vec3d(0, direction1.z+direction1.x, -direction1.y),//axisH
                    1, 2
            );


            portal2 = Portal.entityType.create(world);
            assert portal2 != null;
            portal2.setOriginPos(new Vec3d(blockPos2.getX() + .5 + (direction2.x / 1.99), blockPos2.getY()+ (direction2.y*1.01), blockPos2.getZ() + .5 + (direction2.z / 1.99)-(direction2.y/2)));
            //portal2.setDestination(new Vec3d(blockPos2.getX() + .5 + (direction2.x / 1.96), blockPos2.getY(), blockPos2.getZ() + .5 + (direction2.z / 1.96)));
            portal2.setDestinationDimension(World.OVERWORLD);
            portal2.setOrientationAndSize(
                    new Vec3d(direction2.z+direction2.y, 0, -(direction2.x)),//axisW
                    new Vec3d(0, direction2.z+direction2.x, -direction2.y),//axisH
                    1,//width
                    2//height
            );
        }

        if (blockPos != null && blockPos2 != null) {

            portal.rotation=quion2;
            portal2.rotation=quion1;
            portal.setDestination(portal2.getOriginPos());
            portal2.setDestination(portal.getOriginPos());
            //portal = PortalAPI.createFlippedPortal(portal);
            //portal2 = PortalAPI.createFlippedPortal(portal2);
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