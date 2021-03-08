package com.fusionflux.fluxtech.items;


import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
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

import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
    public Quaternion quion3;
    public Quaternion quion4;
    public float yqvalue;
    public float wqvalue;
    public float yqvalue2;
    public float wqvalue2;
public int testv =0;


    public PortalGun(Settings settings) {
        super(settings);
    }

    public static Quaternion convertQuaternion(double x, double y, double z, double w) {
        return new DQuaternion(x, y, z, w).toMcQuaternion();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult hitResult = user.raycast(128.0D, 0.0F, false);
        if (!user.isSneaking()) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) hitResult).getBlockPos();
                direction1 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                //quion2 = new Quaternion(((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion());
                yqvalue2 = ((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getY();
                wqvalue2 = ((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getW();
            }
        }
        if (user.isSneaking()) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos2 = ((BlockHitResult) hitResult).getBlockPos();
                direction2 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                //quion1 = new Quaternion(Direction.DOWN.getRotationQuaternion().getX(),((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getY(),((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getZ(),((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getW());
                //quion1=Direction.SOUTH.getRotationQuaternion();
                yqvalue = ((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getY();
                wqvalue = ((BlockHitResult)hitResult).getSide().getOpposite().getRotationQuaternion().getW();
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
                    new Vec3d(direction2.z+direction2.y, 0, -(direction2.x)),//axisW,//axisW
                    new Vec3d(0, -1, direction2.y),//axisH
                    1,//width
                    2//height
            );
            portalholder1=PortalAPI.createFlippedPortal(portal2);
            portal2.setOrientationAndSize(
                    new Vec3d(direction1.z+direction1.y, 0, -(direction1.x)),//axisW,//axisW
                    new Vec3d(0, -1, direction1.y),//axisH
                    1, 2
            );
            portalholder2=PortalAPI.createReversePortal(portal2);

            quion1= PortalManipulation.getPortalOrientationQuaternion(new Vec3d(direction2.z+direction2.y, 0, -(direction2.x)),new Vec3d(0, -1, direction2.y)).toMcQuaternion();
            quion2= PortalManipulation.getPortalOrientationQuaternion(new Vec3d(direction1.z+direction1.y, 0, -(direction1.x)),new Vec3d(0, -1, direction1.y)).toMcQuaternion();

            quion3 =convertQuaternion(quion1.getX(),1,quion1.getZ(),0);
            quion4 =convertQuaternion(quion2.getX(),1,quion2.getZ(),0);
        }

        if (blockPos != null && blockPos2 != null) {
            portalholder1.rotation = quion4;
            portalholder2.rotation = quion3;
            makeRoundPortal(portalholder1);
            makeRoundPortal(portalholder2);
            if (!user.world.isClient()) {
                portalholder1.world.spawnEntity(portalholder1);
                portalholder2.world.spawnEntity(portalholder2);

            }
            System.out.println("made portals");
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    public static void makeRoundPortal(Portal portal) {
        GeometryPortalShape shape = new GeometryPortalShape();
        final int triangleNum = 30;
        double twoPi = Math.PI * 2;
        shape.triangles = IntStream.range(0, triangleNum)
                .mapToObj(i -> new GeometryPortalShape.TriangleInPlane(
                        0, 0,
                        portal.width * 0.5 * Math.cos(twoPi * ((double) i) / triangleNum),
                        portal.height * 0.5 * Math.sin(twoPi * ((double) i) / triangleNum),
                        portal.width * 0.5 * Math.cos(twoPi * ((double) i + 1) / triangleNum),
                        portal.height * 0.5 * Math.sin(twoPi * ((double) i + 1) / triangleNum)
                )).collect(Collectors.toList());
        portal.specialShape = shape;
        portal.cullableXStart = 0;
        portal.cullableXEnd = 0;
        portal.cullableYStart = 0;
        portal.cullableYEnd = 0;
    }

}