package com.fusionflux.fluxtech.items;


import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.portal.Portal;

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

    public BlockPos blockPos;
    public BlockPos blockPos2;
    public static Portal portal2;
    public static Portal portalholder1;
    public static Portal portalholder2;
    public Vec3d direction1;
    public Vec3d direction2;
    public Direction testdirection;
    public Quaternion quion1;
    public Quaternion quion2;
public int testv =0;


    public PortalGun(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            HitResult hitResult2 = user.raycast(128.0D, 0.0F, false);
            if (hitResult2.getType() == HitResult.Type.BLOCK) {
                blockPos2 = ((BlockHitResult) hitResult2).getBlockPos();
                direction2 = new Vec3d(((BlockHitResult) hitResult2).getSide().getVector().getX(), ((BlockHitResult) hitResult2).getSide().getVector().getY(), ((BlockHitResult) hitResult2).getSide().getVector().getZ());
                quion1 = new Quaternion(((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion());
                //quion1.set(((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion().getX()+90,((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion().getY(),((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion().getZ(),((BlockHitResult)hitResult2).getSide().getOpposite().getRotationQuaternion().getW());
            }
        } else if (!user.isSneaking()) {
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) hitResult).getBlockPos();
                direction1 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                testdirection = (((BlockHitResult) hitResult).getSide());
                quion2 = testdirection.getRotationQuaternion();

                //quion2 = new Quaternion(((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion());

            }
        }

        if (blockPos != null && blockPos2 != null) {

            if (portalholder1 != null && portalholder2 != null) {
                portalholder1.kill();
                portalholder2.kill();
                System.out.println("removed portals");
            }

            portal2 = Portal.entityType.create(world);
            assert portal2 != null;
            portal2.setOriginPos(new Vec3d(blockPos2.getX() + .5 + (direction2.x / 1.99), blockPos2.getY()+ (direction2.y*1.01), blockPos2.getZ() + .5 + (direction2.z / 1.99)-(direction2.y/2)));
            portal2.setDestination(new Vec3d(blockPos.getX() + .5 + (direction1.x / 1.99), blockPos.getY()+ (direction1.y*1.01), blockPos.getZ() + .5 + (direction1.z / 1.99)-(direction1.y/2)));
            portal2.setDestinationDimension(World.OVERWORLD);
            System.out.println(direction2);
            portal2.setOrientationAndSize(
                    new Vec3d(direction2.z+direction2.y, 0, (direction2.x)),//axisW,//axisW
                    new Vec3d(0, direction2.z+direction2.x, direction2.y),//axisH
                    1,//width
                    2//height
            );
            portalholder1=PortalAPI.createFlippedPortal(portal2);
            portal2.setOrientationAndSize(
                    new Vec3d(direction1.z+direction1.y, 0, (direction1.x)),//axisW,//axisW
                    new Vec3d(0, direction1.z+direction1.x, direction1.y),//axisH
                    1, 2
            );
            portalholder2=PortalAPI.createReversePortal(portal2);
            /*if (testv == 0) {
               // quion1 = portalholder1.getRotation();
                //quion2 = portalholder2.getRotation();
            }
            testv++;
            if(testv==2){
                testv=0;
            }
            //portalholder1.rotation = quion2;
            //portalholder2.rotation = quion1;*/
        }

        if (blockPos != null && blockPos2 != null) {
            if (!user.world.isClient()) {
                portalholder1.world.spawnEntity(portalholder1);
                portalholder2.world.spawnEntity(portalholder2);
                portalholder1.reloadAndSyncToClient();
                portalholder2.reloadAndSyncToClient();
            }
            System.out.println("made portals");
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}