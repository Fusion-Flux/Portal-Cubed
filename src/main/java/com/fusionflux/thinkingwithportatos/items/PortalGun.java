package com.fusionflux.thinkingwithportatos.items;


import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.Portal;

import com.qouteall.immersive_portals.portal.PortalManipulation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.commons.codec.binary.Hex;


public class PortalGun extends Item implements DyeableItem {

    private BlockPos blockPos1;
    private BlockPos blockPos2;
    public Portal portalholder1;
    public Portal portalholder2;
    public Vec3i dirUp1;
    public Vec3i dirUp2;
    public Vec3i dirOut1;
    public Vec3i dirOut2;
    public Vec3i dirRight1;
    public Vec3i dirRight2;
    private int ColorMain;
    private boolean switchedColorCheck=false;
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

@Override
public int getColor(ItemStack stack) {
    CompoundTag compoundTag = stack.getSubTag("display");
    return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : -14842149;
}

    public TypedActionResult<ItemStack> useImpl(World world, PlayerEntity user, Hand hand, boolean leftClick) {
        if (!world.isClient) {
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                if (leftClick) {
                    ColorMain=getColor(user.getStackInHand(Hand.MAIN_HAND));
                    blockPos1 = ((BlockHitResult) hitResult).getBlockPos();
                    dirOut1 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if( dirOut1.getY() == 0 ) {
                        dirUp1 = new Vec3i( 0, 1, 0 );
                    }
                    else {
                        dirUp1 = user.getHorizontalFacing().getVector();
                    }
                    dirRight1 = dirUp1.crossProduct( dirOut1 );

                    ColorMain=getColor(user.getStackInHand(Hand.MAIN_HAND));
                    setColor(user.getStackInHand(Hand.MAIN_HAND),ColorMain);
                    world.playSound(null,user.getPos().getX(),user.getPos().getY(),user.getPos().getZ(),ThinkingWithPortatosSounds.FIRE_EVENT_PRIMARY, SoundCategory.NEUTRAL, .3F, 1F);
                    if(portalholder1 !=null&& portalholder1.isAlive()){
                        world.playSound(null,portalholder1.getPos().getX(),portalholder1.getPos().getY(),portalholder1.getPos().getZ(),ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    }
                } else {
                    blockPos2 = ((BlockHitResult) hitResult).getBlockPos();
                    dirOut2 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if( dirOut2.getY() == 0 ) {
                        dirUp2 = new Vec3i( 0, 1, 0 );
                    }
                    else {
                        dirUp2 = user.getHorizontalFacing().getVector();
                    }
                    dirRight2 = dirUp2.crossProduct( dirOut2 );

                    //if(ColorMain)

                    if(!switchedColorCheck) {
                        ColorMain = getColor(user.getStackInHand(Hand.MAIN_HAND));
                        switchedColorCheck=true;
                    }

                    setColor(user.getStackInHand(Hand.MAIN_HAND),ColorMain*-1);
                    world.playSound(null,user.getPos().getX(),user.getPos().getY(),user.getPos().getZ(),ThinkingWithPortatosSounds.FIRE_EVENT_SECONDARY, SoundCategory.NEUTRAL, .3F, 1F);
                    if(portalholder2 !=null && portalholder2.isAlive()){
                        world.playSound(null,portalholder2.getPos().getX(),portalholder2.getPos().getY(),portalholder2.getPos().getZ(),ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    }
                }
            }else{
                world.playSound(null,user.getPos().getX(),user.getPos().getY(),user.getPos().getZ(),ThinkingWithPortatosSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
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

                Vec3d portalPos1 = calcPortalPos( blockPos1, dirUp1, dirOut1, dirRight1 );
                Vec3d portalPos2 = calcPortalPos( blockPos2, dirUp2, dirOut2, dirRight2 );

                // portal 1
                portalBase.setOriginPos( portalPos1 );
                portalBase.setDestination( portalPos2 );
                portalBase.setDestinationDimension(World.OVERWORLD);
                portalBase.setOrientationAndSize(
                        Vec3d.of( dirRight1 ), //axisW
                        Vec3d.of( dirUp1 ), //axisH
                        .9, // width
                        1.9 // height
                );
                portalholder1 = PortalAPI.createFlippedPortal( portalBase );

                // portal 2
                portalBase.setOriginPos( portalPos2 );
                portalBase.setDestination( portalPos1 );
                portalBase.setOrientationAndSize(
                        Vec3d.of( dirRight2 ), //axisW
                        Vec3d.of( dirUp2 ), //axisH
                        .9, // width
                        1.9 // height
                );
                portalholder2 = PortalAPI.createFlippedPortal( portalBase );

                portalholder2.setRotationTransformation( alignPortal( portalholder1, portalholder2 ).toMcQuaternion() );
                portalholder1.setRotationTransformation( alignPortal( portalholder2, portalholder1 ).toMcQuaternion() );

                //portalholder1.isGlobalPortal = true;
                //portalholder2.isGlobalPortal = true;


                world.spawnEntity(portalholder1);
                world.spawnEntity(portalholder2);

                world.playSound(null,portalholder1.getPos().getX(),portalholder1.getPos().getY(),portalholder1.getPos().getZ(),ThinkingWithPortatosSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, .1F, 1F);
                world.playSound(null,portalholder2.getPos().getX(),portalholder2.getPos().getY(),portalholder2.getPos().getZ(),ThinkingWithPortatosSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, .1F, 1F);

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
        double faceOffset = -0.505;
        double crossOffset = 0.0;
        return new Vec3d(
                ((hit.getX() + 0.5) + upOffset * upright.getX() + faceOffset * facing.getX() + crossOffset * cross.getX()), // x component
                ((hit.getY() + 0.5) + upOffset * upright.getY() + faceOffset * facing.getY() + crossOffset * cross.getY()), // y component
                ((hit.getZ() + 0.5) + upOffset * upright.getZ() + faceOffset * facing.getZ() + crossOffset * cross.getZ())  // z component
        );

    }

    /**
     * The implementation of this method is intended specifically for the use case of entering through
     * a given portal and leaving through another portal with both portals having an arbitrary implementation.
     * The axisW parameter of the out portal is inverted to represent that the transformation should involve
     * leaving away from the output portal (as inverting the axisW also inverts the normal vector).
     *
     * @param from the portal to be entered.
     * @param to the portal to be exited from.
     * @return the unit quaternion representing the rotation from a portal to the other.
     */
    private DQuaternion alignPortal( Portal from, Portal to ) {
        DQuaternion in = PortalManipulation.getPortalOrientationQuaternion( from.axisW, from.axisH );
        DQuaternion out = PortalManipulation.getPortalOrientationQuaternion( inv3d(to.axisW), to.axisH );

        DQuaternion point;
        point = q1TimesQ2( in, out.getConjugated() );
        // something else supposedly happens here

        return point.getNormalized();
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

    /**
     * @param vec the vector to invert.
     * @return an inversion of the input vector.
     */
    private Vec3i inv3i( Vec3i vec ) {
        return new Vec3i( -vec.getX(), -vec.getY(), -vec.getZ() );
    }

    /**
     * @param vec the vector to invert.
     * @return an inversion of the input vector.
     */
    private Vec3d inv3d( Vec3d vec ) {
        return new Vec3d( -vec.getX(), -vec.getY(), -vec.getZ() );
    }
}