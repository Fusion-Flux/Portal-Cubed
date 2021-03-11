package com.fusionflux.fluxtech.items;


import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.Portal;

import com.qouteall.immersive_portals.portal.PortalManipulation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;


public class PortalGun extends Item {

    // TODO
    // All of these fields NEED to be moved to some other stored value,
    // either nbt or a persistent state.
    private BlockPos blockPos1;
    private BlockPos blockPos2;
    public Portal portalholder1;
    public Portal portalholder2;
    public Vec3i dirUpright1;
    public Vec3i dirUpright2;
    public Vec3i dirFacing1;
    public Vec3i dirFacing2;
    public Vec3i cross1;
    public Vec3i cross2;

    public PortalGun(Settings settings) {
        super(settings);
    }

    /**
     * Called when a user left clicks with an {@link ItemStack} of a {@link FluxTechItems#PORTAL_GUN} in hand.
     * NOTE: Called serverside only.
     *
     * @author Platymemo
     */
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
                    blockPos1 = ((BlockHitResult) hitResult).getBlockPos();
                    dirFacing1 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if( dirFacing1.getY() == 0 ) {
                        dirUpright1 = new Vec3i( 0, 1, 0 );
                    }
                    else {
                        dirUpright1 = user.getHorizontalFacing().getVector();
                    }
                    cross1 = dirUpright1.crossProduct( dirFacing1 );
                } else {
                    blockPos2 = ((BlockHitResult) hitResult).getBlockPos();
                    dirFacing2 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if( dirFacing2.getY() == 0 ) {
                        dirUpright2 = new Vec3i( 0, 1, 0 );
                    }
                    else {
                        dirUpright2 = user.getHorizontalFacing().getVector();
                    }
                    cross2 = dirUpright2.crossProduct( dirFacing2 );
                }
            }
            if ( blockPos1 != null && blockPos2 != null) {

                Portal portalBase;

                if (portalholder1 != null && portalholder2 != null) {
                    portalholder1.kill();
                    portalholder2.kill();
                    // System.out.println("removed portals");
                }

                portalBase = Portal.entityType.create(world);

                // Should never be null unless something is very wrong
                assert portalBase != null;

                Vec3d portalPos1 = calcPortalPos( blockPos1, dirUpright1, dirFacing1, cross1 );
                Vec3d portalPos2 = calcPortalPos( blockPos2, dirUpright2, dirFacing2, cross2 );

                portalBase.setOriginPos( portalPos2 );
                portalBase.setDestination( portalPos1 );
                portalBase.setDestinationDimension(World.OVERWORLD);
                portalBase.setOrientationAndSize(
                        new Vec3d( cross2.getX(), cross2.getY(), cross2.getZ() ), //axisW
                        new Vec3d( dirUpright2.getX(), dirUpright2.getY(), dirUpright2.getZ() ), //axisH
                        1, // width
                        2 // height
                );
                portalholder1 = PortalAPI.createFlippedPortal( portalBase );
                portalBase.setOrientationAndSize(
                        new Vec3d( cross1.getX(), cross1.getY(), cross1.getZ() ), //axisW
                        new Vec3d( dirUpright1.getX(), dirUpright1.getY(), dirUpright1.getZ() ), //axisH
                        1, // width
                        2 // height
                );
                portalholder2 = PortalAPI.createReversePortal( portalBase );

                alignPortalsTest2();

                world.spawnEntity(portalholder1);
                world.spawnEntity(portalholder2);

                portalholder1.reloadAndSyncToClient();
                portalholder2.reloadAndSyncToClient();
            }
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    /**
     * @param hit the position designated by the player's input for a given portal.
     * @param upright the upright axial vector of the portal based on placement context.
     * @param facing the facing axial vector of the portal based on placement context.
     * @param cross the cross product of upright x facing.
     * @return a vector position specifying the portal's final position in the world.
     */
    private Vec3d calcPortalPos( BlockPos hit, Vec3i upright, Vec3i facing, Vec3i cross ) {
        double upOffset = -0.5;
        double faceOffset = -0.5005;
        double crossOffset = 0.0;
        return new Vec3d(
                ((hit.getX() + 0.5) + upOffset * upright.getX() + faceOffset * facing.getX() + crossOffset * cross.getX()), // x component
                ((hit.getY() + 0.5) + upOffset * upright.getY() + faceOffset * facing.getY() + crossOffset * cross.getY()), // y component
                ((hit.getZ() + 0.5) + upOffset * upright.getZ() + faceOffset * facing.getZ() + crossOffset * cross.getZ())  // z component
        );

    }

    /**
     * oh god
     */
    private void alignPortalsTest1() {
        // Quaternion time, let's declare some
        DQuaternion dQuionIn1 = PortalManipulation.getPortalOrientationQuaternion( portalholder1.axisW, portalholder1.axisH ).getNormalized();
        DQuaternion dQuionIn2 = PortalManipulation.getPortalOrientationQuaternion( portalholder2.axisW, portalholder2.axisH ).getNormalized();
        DQuaternion dQuionOut1 = PortalManipulation.getPortalOrientationQuaternion( portalholder1.axisW, portalholder1.axisH ).getNormalized();
        DQuaternion dQuionOut2 = PortalManipulation.getPortalOrientationQuaternion( portalholder2.axisW, portalholder2.axisH ).getNormalized();

        dQuionIn1 = q1TimesQ2( dQuionOut1, q1TimesQ2( dQuionIn1, dQuionOut1.getConjugated() ));
        dQuionIn2 = q1TimesQ2( dQuionOut2, q1TimesQ2( dQuionIn2, dQuionOut2.getConjugated() ));

        Quaternion quion1 = dQuionIn1.toMcQuaternion();
        Quaternion quion2 = dQuionIn2.toMcQuaternion();

        portalholder1.setRotationTransformation( quion2 );
        portalholder2.setRotationTransformation( quion1 );
    }

    /**
     * ohH GOD
     */
    private void alignPortalsTest2() {
        DQuaternion dQuion1 = DQuaternion.getRotationBetween( portalholder1.axisH, portalholder2.axisH ).getNormalized();
        DQuaternion dQuion2 = DQuaternion.getRotationBetween( portalholder1.axisW, portalholder2.axisW.negate() ).getNormalized();
        DQuaternion dQuion3 = DQuaternion.getRotationBetween( portalholder2.axisH, portalholder1.axisH ).getNormalized();
        DQuaternion dQuion4 = DQuaternion.getRotationBetween( portalholder2.axisW, portalholder1.axisW.negate() ).getNormalized();

        Quaternion quion1 = q1TimesQ2( dQuion2, dQuion1 ).toMcQuaternion();
        Quaternion quion2 = q1TimesQ2( dQuion4, dQuion3 ).toMcQuaternion();

        portalholder1.setRotationTransformation(quion1);
        portalholder2.setRotationTransformation(quion2);
    }

    /**
     * I believe the methods for products supplied by Mojang and quoteall are correct,
     * but I needed to be able to see things in a different visual format so I went ahead and implemented
     * a version of the product here that I find to be easier to think in terms of. This will likely be
     * unneeded as long as every other part of the code structure is correct, and thus this can be removed
     * once the desired behavior for quaternion-based rotations works consistently.
     *
     * @param q1 the quaternion whose rotation will be applied second.
     * @param q2 the quaternion whose rotation will be applied first.
     * @return the product q1 * q2.
     */
    private static DQuaternion q1TimesQ2( DQuaternion q1, DQuaternion q2 ) {
        double a = q1.getW();
        double b = q1.getX();
        double c = q1.getY();
        double d = q1.getZ();
        double e = q2.getW();
        double f = q2.getX();
        double g = q2.getY();
        double h = q2.getZ();

        return new DQuaternion(
                ( b*e + a*f + c*h - d*g ),
                ( a*g - b*h + c*e + d*f ),
                ( a*h + b*g - c*f + d*e ),
                ( a*e - b*f - c*g - d*h )
        );
    }
}