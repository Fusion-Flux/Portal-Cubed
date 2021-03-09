package com.fusionflux.fluxtech.items;


import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.GeometryPortalShape;
import com.qouteall.immersive_portals.portal.Portal;

import com.qouteall.immersive_portals.portal.PortalManipulation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PortalGun extends Item {

    private BlockPos blockPos;
    private BlockPos blockPos2;
    public static Portal portalholder1;
    public static Portal portalholder2;
    public Vec3d direction1;
    public Vec3d direction2;
    public float yqvalue;
    public float wqvalue;
    public float yqvalue2;
    public float wqvalue2;

    public PortalGun(Settings settings) {
        super(settings);
    }

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        useImpl(world, user, hand, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return useImpl(world, user, hand, false);
    }

    public TypedActionResult<ItemStack> useImpl(World world, PlayerEntity user, Hand hand, boolean leftClick) {
        if (!world.isClient) {
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                if (leftClick) {
                    blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    direction1 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                    yqvalue2 = ((BlockHitResult) hitResult).getSide().getOpposite().getRotationQuaternion().getY();
                    wqvalue2 = ((BlockHitResult) hitResult).getSide().getOpposite().getRotationQuaternion().getW();
                } else {
                    blockPos2 = ((BlockHitResult) hitResult).getBlockPos();
                    direction2 = new Vec3d(((BlockHitResult) hitResult).getSide().getVector().getX(), ((BlockHitResult) hitResult).getSide().getVector().getY(), ((BlockHitResult) hitResult).getSide().getVector().getZ());
                    yqvalue = ((BlockHitResult) hitResult).getSide().getOpposite().getRotationQuaternion().getY();
                    wqvalue = ((BlockHitResult) hitResult).getSide().getOpposite().getRotationQuaternion().getW();
                }
            }
            if (blockPos != null && blockPos2 != null) {

                Portal portalBase;

                if (portalholder1 != null && portalholder2 != null) {
                    portalholder1.kill();
                    portalholder2.kill();
                    // System.out.println("removed portals");
                }

                portalBase = Portal.entityType.create(world);

                // Should never be null unless something is very wrong
                assert portalBase != null;

                portalBase.setOriginPos(new Vec3d(blockPos2.getX() + .5 + (direction2.x / 1.99), blockPos2.getY() + (direction2.y * 1.01), blockPos2.getZ() + .5 + (direction2.z / 1.99) - (direction2.y / 2)));
                portalBase.setDestination(new Vec3d(blockPos.getX() + .5 + (direction1.x / 1.99), blockPos.getY() + (direction1.y * 1.01), blockPos.getZ() + .5 + (direction1.z / 1.99) - (direction1.y / 2)));
                portalBase.setDestinationDimension(World.OVERWORLD);
                portalBase.setOrientationAndSize(
                        new Vec3d(direction2.z + direction2.y, 0, -(direction2.x)), //axisW
                        new Vec3d(0, -1, direction2.y), //axisH
                        1, //width
                        2 //height
                );
                portalholder1 = PortalAPI.createFlippedPortal(portalBase);
                portalBase.setOrientationAndSize(
                        new Vec3d(direction1.z + direction1.y, 0, -(direction1.x)), //axisW
                        new Vec3d(0, -1, direction1.y), //axisH
                        1, 2
                );
                portalholder2 = PortalAPI.createReversePortal(portalBase);

                // TODO
                // get these to return quaternions for use in rotating
                // Quaternion quion1;
                // Quaternion quion2;
                // quion1 = PortalManipulation.getPortalOrientationQuaternion(new Vec3d(direction2.z+direction2.y, 0, -(direction2.x)),new Vec3d(0, -1, direction2.y)).toMcQuaternion();
                // quion2 = PortalManipulation.getPortalOrientationQuaternion(new Vec3d(direction1.z+direction1.y, 0, -(direction1.x)),new Vec3d(0, -1, direction1.y)).toMcQuaternion();


                // TODO
                // Un-hardcode these values
                /*---------------------
                Quaternion quion3 = new Quaternion( new Vector3f(portalholder1.axisH), (float)(portalholder2.axisW.dotProduct( portalholder1.axisW )/(portalholder1.axisW.length() * portalholder2.axisW.length())), true);
                quion3.normalize();
                Quaternion quion4 = new Quaternion( new Vector3f(portalholder2.axisH),  (float)(portalholder1.axisW.dotProduct( portalholder2.axisW )/(portalholder2.axisW.length() * portalholder1.axisW.length())), true);
                quion4.normalize();
                //--------------------*/

                DQuaternion dQuion5 = PortalManipulation.getPortalOrientationQuaternion(portalholder1.axisW, portalholder1.axisH);
                DQuaternion dQuion6 = PortalManipulation.getPortalOrientationQuaternion(portalholder2.axisW, portalholder2.axisH);
                DQuaternion tempDQuion = dQuion5;

                dQuion5 = ((dQuion5.getConjugated()).hamiltonProduct(dQuion6));
                Quaternion quion5 = dQuion5.getNormalized().toMcQuaternion();

                dQuion6 = ((dQuion6.getConjugated()).hamiltonProduct(tempDQuion));
                Quaternion quion6 = dQuion6.getNormalized().toMcQuaternion();

                //quion6.normalize();
                //Quaternion quionTemp = quion5;

                /*----------
                quion5.hamiltonProduct( quion6 );
                quion5.normalize();
                quion6.conjugate();
                quion6.hamiltonProduct( quionTemp );
                quion6.normalize();
                //--------*/

                portalholder1.setRotationTransformation(quion5);
                portalholder2.setRotationTransformation(quion6);
                System.out.println(portalholder1.rotation);
                System.out.println(portalholder2.rotation);


                // commented out for now as it just fills the portal data with too much text
                //makeRoundPortal(portalholder1);
                //makeRoundPortal(portalholder2);

                world.spawnEntity(portalholder1);
                world.spawnEntity(portalholder2);
                // System.out.println("made portals");

                // sail stuff
                portalholder1.reloadAndSyncToClient();
                portalholder2.reloadAndSyncToClient();
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    public static void makeRoundPortal(Portal portal) {
        GeometryPortalShape shape = new GeometryPortalShape();
        final int triangleCount = 30;
        double twoPi = Math.PI * 2;
        shape.triangles = IntStream.range(0, triangleCount)
                .mapToObj(x -> new GeometryPortalShape.TriangleInPlane(
                        0, 0,
                        portal.width * MathHelper.cos((float) twoPi * (x) / triangleCount) / 2,
                        portal.height * MathHelper.sin((float) twoPi * (x) / triangleCount) / 2,
                        portal.width * MathHelper.cos((float) twoPi * (x + 1) / triangleCount) / 2,
                        portal.height * MathHelper.sin((float) twoPi * (x + 1) / triangleCount) / 2
                )).collect(Collectors.toList());
        portal.specialShape = shape;
        portal.cullableXStart = 0;
        portal.cullableXEnd = 0;
        portal.cullableYStart = 0;
        portal.cullableYEnd = 0;
    }

}