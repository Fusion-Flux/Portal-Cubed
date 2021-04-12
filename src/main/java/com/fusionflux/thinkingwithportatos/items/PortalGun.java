package com.fusionflux.thinkingwithportatos.items;


import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.PortalManipulation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
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

    public PortalGun(Settings settings) {
        super(settings);
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
        useImpl(world, user, stack, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && ThinkingWithPortatos.getBodyGrabbingManager(false).tryStopGrabbing(user)) {
            return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
        }

        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", true);
        return useImpl(world, user, stack, false);
    }

    public TypedActionResult<ItemStack> useImpl(World world, PlayerEntity user, ItemStack stack, boolean leftClick) {
            if (!world.isClient) {
                CompoundTag tag = stack.getOrCreateTag();

                PortalPlaceholderEntity portalOutline;
                CustomPortalEntity portalholder;
                CompoundTag portalsTag = tag.getCompound(world.getRegistryKey().toString());

                boolean outlineExists = false;
                if (portalsTag.contains((leftClick ? "Left" : "Right") + "Background")) {
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Left" : "Right") + "Background"));

                    if (portalOutline == null) {
                        portalOutline = ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER.create(world);
                    } else {
                        outlineExists = true;
                    }
                } else {
                    portalOutline = ThinkingWithPortatosEntities.PORTAL_PLACEHOLDER.create(world);
                }

                boolean portalExists = false;
                if (portalsTag.contains((leftClick ? "Left" : "Right") + "Portal")) {
                    portalholder = (CustomPortalEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Left" : "Right") + "Portal"));
                    if (portalholder == null) {
                        portalholder = ThinkingWithPortatosEntities.CUSTOM_PORTAL.create(world);
                    } else {
                        portalExists = true;
                    }
                } else {
                    portalholder = ThinkingWithPortatosEntities.CUSTOM_PORTAL.create(world);
                }

                CustomPortalEntity otherPortal;
                if (portalsTag.contains((leftClick ? "Right" : "Left") + "Portal")) {
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid((leftClick ? "Right" : "Left") + "Portal"));
                } else {
                    otherPortal = null;
                }

                Vec3i up;
                Vec3i normal;
                Vec3i right;
                BlockPos blockPos;
                HitResult hitResult = user.raycast(128.0D, 0.0F, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    //Block.isFaceFullSquare(world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getCollisionShape(world,((BlockHitResult) hitResult).getBlockPos()),((BlockHitResult) hitResult).getSide().getOpposite());
                    blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    normal = ((BlockHitResult) hitResult).getSide().getOpposite().getVector();
                    if (normal.getY() == 0) {
                        up = new Vec3i(0, 1, 0);
                    } else {
                        up = user.getHorizontalFacing().getVector();
                    }
                    right = up.crossProduct(normal);

                    world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), ThinkingWithPortatosSounds.FIRE_EVENT_PRIMARY, SoundCategory.NEUTRAL, .3F, 1F);
                    if (portalholder != null && portalholder.isAlive()) {
                        world.playSound(null, portalholder.getPos().getX(), portalholder.getPos().getY(), portalholder.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    }

                    Vec3d placeholderPos1 = calcPos(blockPos, up, normal, right, true);
                    Pair<Double, Double> rotAngles = DQuaternion.getPitchYawFromRotation(PortalManipulation.getPortalOrientationQuaternion(Vec3d.of(right), Vec3d.of(up)));
                    assert portalOutline != null;
                    portalOutline.setPos(placeholderPos1.x, placeholderPos1.y, placeholderPos1.z);
                    portalOutline.yaw = rotAngles.getLeft().floatValue() + (90 * up.getX());
                    portalOutline.pitch = rotAngles.getRight().floatValue();
                    portalOutline.setRoll((rotAngles.getRight().floatValue() + (90)) * up.getX());
                    portalOutline.setColor(this.getColor(stack));
                    portalOutline.noClip = true;
                    if (!outlineExists) {
                        world.spawnEntity(portalOutline);
                    }
                    Vec3d portalPos1 = calcPos(blockPos, up, normal, right, false);

                    assert portalholder != null;
                    portalholder.setOriginPos(portalPos1);
                    portalholder.setDestination(portalPos1);
                    portalholder.setDestinationDimension(world.getRegistryKey());
                    portalholder.setOrientationAndSize(
                            Vec3d.of(right), //axisW
                            Vec3d.of(up).multiply(-1), //axisH
                            .9, // width
                            1.9 // height
                    );
                    portalholder.setOutline(portalOutline.getUuidAsString());
                    portalOutline.axisH=portalholder.axisH;
                    portalOutline.axisW=portalholder.axisW;

                    if(portalExists&&otherPortal==null) {
                        PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(portalholder),portalholder);
                        portalholder.reloadAndSyncToClient();

                    }
                    if (!portalExists) {
                        portalholder.setString("null");
                        world.spawnEntity(portalholder);
                    }
                    if (otherPortal != null) {

                        portalholder.setDestination(otherPortal.getOriginPos());
                        otherPortal.setDestination(portalholder.getOriginPos());
                        portalholder.setActive(true);
                        otherPortal.setActive(true);
                        portalholder.setString(otherPortal.getUuidAsString());
                        otherPortal.setString(portalholder.getUuidAsString());

                        PortalManipulation.adjustRotationToConnect(portalholder, otherPortal);

                        otherPortal.reloadAndSyncToClient();
                        portalholder.reloadAndSyncToClient();

                        world.playSound(null, portalholder.getPos().getX(), portalholder.getPos().getY(), portalholder.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_OPEN, SoundCategory.NEUTRAL, .1F, 1F);
                    }
                } else {
                    world.playSound(null, user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), ThinkingWithPortatosSounds.INVALID_PORTAL_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                }

                assert portalholder != null;
                portalsTag.putUuid((leftClick ? "Left" : "Right") + "Portal", portalholder.getUuid());
                assert portalOutline != null;
                portalsTag.putUuid((leftClick ? "Left" : "Right") + "Background", portalOutline.getUuid());

                tag.put(world.getRegistryKey().toString(), portalsTag);


        }
        return TypedActionResult.pass(stack);
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