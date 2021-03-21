package com.fusionflux.thinkingwithportatos.items;


import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
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
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;


public class PortalGun extends Item implements DyeableItem {

    public Portal portalholder1;
    public PortalPlaceholderEntity portalOutline1;
    public Portal portalholder2;
    public PortalPlaceholderEntity portalOutline2;
    public Vec3i dirUp1;
    public Vec3i dirUp2;
    public Vec3i dirOut1;
    public Vec3i dirOut2;
    public Vec3i dirRight1;
    public Vec3i dirRight2;
    public boolean portalsActivated = false;
    public int color1 = 0;
    public int color2 = 0;
    private BlockPos blockPos1;
    private BlockPos blockPos2;

    public PortalGun(Settings settings) {
        super(settings);
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
    private static DQuaternion q1TimesQ2(DQuaternion q1, DQuaternion q2) {
        double a = q1.getW();
        double b = q1.getX();
        double c = q1.getY();
        double d = q1.getZ();
        double e = q2.getW();
        double f = q2.getX();
        double g = q2.getY();
        double h = q2.getZ();

        return new DQuaternion(
                (b * e + a * f + c * h - d * g),
                (a * g - b * h + c * e + d * f),
                (a * h + b * g - c * f + d * e),
                (a * e - b * f - c * g - d * h)
        );
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        boolean complementary = compoundTag.getBoolean("complementary");
        compoundTag = stack.getSubTag("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? complementary ? compoundTag.getInt("color") * -1 : compoundTag.getInt("color") : (complementary ? 14842149 : -14842149);
    }

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", false);
        useImpl(world, user, hand, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", true);
        return useImpl(world, user, hand, false);
    }

    public TypedActionResult<ItemStack> useImpl(World world, PlayerEntity user, Hand hand, boolean leftClick) {
        if (!world.isClient) {
            if (portalsActivated) {
                assert portalholder1 != null;
                if (!portalholder1.isAlive()) {
                    portalholder2.kill();
                    blockPos1 = null;
                    dirUp1 = null;
                    dirOut1 = null;
                    dirRight1 = null;
                    portalsActivated = false;
                }
                assert portalholder2 != null;
                if (!portalholder2.isAlive()) {
                    portalholder1.kill();
                    blockPos2 = null;
                    dirUp2 = null;
                    dirOut2 = null;
                    dirRight2 = null;
                    portalsActivated = false;
                }
            }
            HitResult hitResult = user.raycast(128.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                if (leftClick) {
                    blockPos1 = ((BlockHitResult) hitResult).getBlockPos();
                    dirOut1 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if (dirOut1.getY() == 0) {
                        dirUp1 = new Vec3i(0, 1, 0);
                    } else {
                        dirUp1 = user.getHorizontalFacing().getVector();
                    }
                    dirRight1 = dirUp1.crossProduct(dirOut1);

                    world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), ThinkingWithPortatosSounds.FIRE_EVENT_PRIMARY, SoundCategory.NEUTRAL, .3F, 1F);
                    if (portalholder1 != null && portalholder1.isAlive()) {
                        world.playSound(null, portalholder1.getPos().getX(), portalholder1.getPos().getY(), portalholder1.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    }
                    color1 = (this.getColor(user.getStackInHand(Hand.MAIN_HAND)));

                    if (portalOutline1 != null)
                        portalOutline1.kill();
                } else {
                    blockPos2 = ((BlockHitResult) hitResult).getBlockPos();
                    dirOut2 = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if (dirOut2.getY() == 0) {
                        dirUp2 = new Vec3i(0, 1, 0);
                    } else {
                        dirUp2 = user.getHorizontalFacing().getVector();
                    }
                    dirRight2 = dirUp2.crossProduct(dirOut2);

                    world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), ThinkingWithPortatosSounds.FIRE_EVENT_SECONDARY, SoundCategory.NEUTRAL, .3F, 1F);
                    if (portalholder2 != null && portalholder2.isAlive()) {
                        world.playSound(null, portalholder2.getPos().getX(), portalholder2.getPos().getY(), portalholder2.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    }

                    color2 = (this.getColor(user.getStackInHand(Hand.MAIN_HAND)));
                    if (portalOutline2 != null)
                        portalOutline2.kill();
                }
            } else {
                world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), ThinkingWithPortatosSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
            }
            if (blockPos1 != null && blockPos2 != null) {

                Portal portalBase;

                if (portalholder1 != null && portalholder2 != null) {
                    portalholder1.kill();
                    portalholder2.kill();
                }

                portalBase = Portal.entityType.create(world);

                // Should never be null unless something is very wrong
                assert portalBase != null;

                Vec3d portalPos1 = calcPos(blockPos1, dirUp1, dirOut1, dirRight1, false);
                Vec3d portalPos2 = calcPos(blockPos2, dirUp2, dirOut2, dirRight2, false);


                // portal 1
                portalBase.setOriginPos(portalPos1);
                portalBase.setDestination(portalPos2);
                portalBase.setDestinationDimension(user.world.getRegistryKey());
                portalBase.setOrientationAndSize(
                        Vec3d.of(dirRight1), //axisW
                        Vec3d.of(dirUp1), //axisH
                        .9, // width
                        1.9 // height
                );
                portalholder1 = PortalAPI.createFlippedPortal(portalBase);

                // portal 2
                portalBase.setOriginPos(portalPos2);
                portalBase.setDestination(portalPos1);
                portalBase.setOrientationAndSize(
                        Vec3d.of(dirRight2), //axisW
                        Vec3d.of(dirUp2), //axisH
                        .9, // width
                        1.9 // height
                );
                portalholder2 = PortalAPI.createFlippedPortal(portalBase);
                //dirOut1=portalholder1.getNormal();
                PortalManipulation.adjustRotationToConnect(portalholder1, portalholder2);

                world.spawnEntity(portalholder1);
                world.spawnEntity(portalholder2);

                //PortalsManager.getPortals().put(user.getUuidAsString() + "portal1",portalholder1);
                // PortalsManager.getPortals().put(user.getUuidAsString() + "portal2",portalholder2);

                portalsActivated = true;


                world.playSound(null, portalholder1.getPos().getX(), portalholder1.getPos().getY(), portalholder1.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, .1F, 1F);
                world.playSound(null, portalholder2.getPos().getX(), portalholder2.getPos().getY(), portalholder2.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, .1F, 1F);

            }

            if (leftClick) {
                Vec3d placeholderPos1 = calcPos(blockPos1, dirUp1, dirOut1, dirRight1, true);
                Pair<Double, Double> rotAngles = DQuaternion.getPitchYawFromRotation(PortalManipulation.getPortalOrientationQuaternion(Vec3d.of(dirRight1), Vec3d.of(dirUp1)));
                portalOutline1 = new PortalPlaceholderEntity(ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER, user.world);
                portalOutline1.setPos(placeholderPos1.x, placeholderPos1.y, placeholderPos1.z);
                portalOutline1.yaw = rotAngles.getLeft().floatValue() + (90 * dirUp1.getX());
                portalOutline1.pitch = rotAngles.getRight().floatValue();
                portalOutline1.setRoll((rotAngles.getRight().floatValue() + (90)) * dirUp1.getX());
                portalOutline1.setColor(color1);
                portalOutline1.noClip = true;
                //portalOutline1.getOrCreateTag().putInt("colorValue",color1);
                world.spawnEntity(portalOutline1);
            } else {
                Vec3d placeholderPos2 = calcPos(blockPos2, dirUp2, dirOut2, dirRight2, true);
                Pair<Double, Double> rotAngles2 = DQuaternion.getPitchYawFromRotation(PortalManipulation.getPortalOrientationQuaternion(Vec3d.of(dirRight2), Vec3d.of(dirUp2)));
                portalOutline2 = new PortalPlaceholderEntity(ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER, user.world);
                portalOutline2.setPos(placeholderPos2.x, placeholderPos2.y, placeholderPos2.z);
                portalOutline2.yaw = rotAngles2.getLeft().floatValue() + (90 * dirUp2.getX());
                portalOutline2.pitch = rotAngles2.getRight().floatValue();
                portalOutline2.setRoll((rotAngles2.getRight().floatValue() + (90)) * dirUp2.getX());
                portalOutline2.setColor(color2);
                portalOutline2.noClip = true;
                //portalOutline1.getOrCreateTag().putInt("colorValue",color2);
                world.spawnEntity(portalOutline2);
            }

        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    /**
     * @param hit          the position designated by the player's input for a given portal.
     * @param upright      the upright axial vector of the portal based on placement context.
     * @param facing       the facing axial vector of the portal based on placement context.
     * @param cross        the cross product of upright x facing.
     * @param isBackground whether or not this is positioning for a {@link PortalPlaceholderEntity}
     * @return a vector position specifying the portal's final position in the world.
     */
    private Vec3d calcPos(BlockPos hit, Vec3i upright, Vec3i facing, Vec3i cross, boolean isBackground) {
        double upOffset = isBackground ? -1.0 : -0.5;
        double faceOffset = isBackground ? -0.509 : -0.510;
        double crossOffset = 0.0;
        return new Vec3d(
                ((hit.getX() + 0.5) + upOffset * upright.getX() + faceOffset * facing.getX() + crossOffset * cross.getX()), // x component
                ((hit.getY() + 0.5) + upOffset * upright.getY() + faceOffset * facing.getY() + crossOffset * cross.getY()), // y component
                ((hit.getZ() + 0.5) + upOffset * upright.getZ() + faceOffset * facing.getZ() + crossOffset * cross.getZ())  // z component
        );

    }
}