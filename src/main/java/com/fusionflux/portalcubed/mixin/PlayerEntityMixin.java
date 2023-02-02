package com.fusionflux.portalcubed.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.fusionflux.portalcubed.util.PortalVelocityHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EntityAttachments {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        throw new AssertionError(
                PortalCubed.MOD_ID + "'s PlayerEntityMixin dummy constructor was called, " +
                        "something is very wrong here!"
        );
    }

    @Shadow
    @Override
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    @Override
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    @Override
    public abstract boolean isSwimming();

    @Shadow @Final private PlayerAbilities abilities;

    @Override
    @Shadow public abstract float getMovementSpeed();

    float storedYaw = 0;

    boolean wasInfiniteFall = false;

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void portalCubed$letYouFallLonger(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack5 = this.getEquippedStack(EquipmentSlot.FEET);
        if (damageSource == DamageSource.FALL && (itemStack5.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS))) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3d portalCubed$what(Vec3d travelVectorOriginal) {
        if (!this.hasNoGravity()) {
            ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
            if (!this.isOnGround() && PortalCubedConfig.enableAccurateMovement && !this.isSwimming() && !this.abilities.flying && !this.isFallFlying() && itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.world.getBlockState(this.getBlockPos()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.world.getBlockState(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {
                double mathVal = 1;
                double horizontalVelocity = Math.abs(this.getVelocity().x) + Math.abs(this.getVelocity().z);
                if (horizontalVelocity / 0.01783440120041885 > 1) {
                    mathVal = horizontalVelocity / 0.01783440120041885;
                }
                travelVectorOriginal = new Vec3d(travelVectorOriginal.x / mathVal, travelVectorOriginal.y, travelVectorOriginal.z / mathVal);
                this.flyingSpeed = .04f;
            }
        }

    return travelVectorOriginal;
    }

    private boolean enableNoDrag2;

    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        PlayerEntity thisentity = ((PlayerEntity) (Object) this);

        if (world.isClient && CalledValues.getHasTeleportationHappened(thisentity)) {
            var byteBuf = PacketByteBufs.create();
            NetworkingSafetyWrapper.sendFromClient("clientteleportupdate", byteBuf);
            CalledValues.setHasTeleportationHappened(thisentity, false);
            ((EntityAttachments) thisentity).setMaxFallHeight(-99999999);
            //this.setYaw(storedYaw);
            this.setVelocity(CalledValues.getVelocityUpdateAfterTeleport(thisentity));
        }

        Vec3d entityVelocity = this.getVelocity();

        Box portalCheckBox = getBoundingBox();
        if (!this.world.isClient) {
            portalCheckBox = portalCheckBox.expand(10);
        } else {
            portalCheckBox = portalCheckBox.stretch(entityVelocity).stretch(entityVelocity.multiply(-1));
        }
        List<ExperimentalPortal> list = ((PlayerEntity) (Object) this).world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);
        VoxelShape omittedDirections = VoxelShapes.empty();

        for (ExperimentalPortal portal : list) {
            if (portal.calculateCuttoutBox() != nullBox) {
                if (portal.getActive())
                    omittedDirections = VoxelShapes.union(omittedDirections, VoxelShapes.cuboid(portal.getCutoutBoundingBox()));
            }
        }
        CalledValues.setPortalCutout(((PlayerEntity) (Object) this), omittedDirections);


        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        if((!this.isOnGround() && PortalCubedConfig.enableAccurateMovement && !this.isSwimming() && !this.abilities.flying && !this.isFallFlying() && itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.world.getBlockState(this.getBlockPos()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.world.getBlockState(new BlockPos(this.getBlockPos().getX(),this.getBlockPos().getY()+1,this.getBlockPos().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL))){
            if(!enableNoDrag2) {
                enableNoDrag2 = true;
            }
                this.setNoDrag(true);
        }else if (enableNoDrag2){
            enableNoDrag2 = false;
            this.setNoDrag(false);
        }

        if(itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS)){
            if(this.getVelocity().y < -3.92){
                this.setVelocity(this.getVelocity().add(0,.81d,0));
            }
        }
    }



    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        PlayerEntity thisentity = ((PlayerEntity) (Object) this);

        Vec3d entityVelocity = this.getVelocity();

        Box portalCheckBox = getBoundingBox();

        portalCheckBox = portalCheckBox.stretch(entityVelocity).stretch(entityVelocity.multiply(-1));


        List<ExperimentalPortal> list = ((PlayerEntity) (Object) this).world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

        for (ExperimentalPortal portal : list) {

            if (this.canUsePortals() && portal.getActive() && this.world.isClient && !CalledValues.getHasTeleportationHappened(thisentity)) {
                Direction portalFacing = portal.getFacingDirection();
                Direction portalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getAxisH(portal).x, CalledValues.getAxisH(portal).y, CalledValues.getAxisH(portal).z));

                Direction otherDirec = Direction.fromVector((int) CalledValues.getOtherFacing(portal).getX(), (int) CalledValues.getOtherFacing(portal).getY(), (int) CalledValues.getOtherFacing(portal).getZ());
                Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getOtherAxisH(portal).x, CalledValues.getOtherAxisH(portal).y, CalledValues.getOtherAxisH(portal).z));

                if (otherDirec != null) {
                    double teleportYOffset = switch (otherDirec) {
                        case DOWN -> 2;
                        case NORTH, SOUTH, EAST, WEST -> {
                            if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                                yield portal.getPos().y - thisentity.getPos().y;
                            } else {
                                yield 1;
                            }
                        }
                        default -> 0;
                    };

                    double teleportXOffset = portal.getPos().x - thisentity.getPos().x;
                    double teleportZOffset = portal.getPos().z - thisentity.getPos().z;

                    if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                        if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                            if (portalVertFacing == Direction.NORTH || portalVertFacing == Direction.SOUTH) {
                                if (otherDirec == Direction.EAST || otherDirec == Direction.WEST) {
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = 0;
                                }
                            }

                            if (portalVertFacing == Direction.EAST || portalVertFacing == Direction.WEST) {
                                if (otherDirec == Direction.NORTH || otherDirec == Direction.SOUTH) {
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = 0;
                                }
                            }
                        }
                    }

                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                            if (otherPortalVertFacing == Direction.NORTH || otherPortalVertFacing == Direction.SOUTH) {
                                if (portalFacing == Direction.EAST || portalFacing == Direction.WEST) {
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = 0;
                                }
                            }

                            if (otherPortalVertFacing == Direction.EAST || otherPortalVertFacing == Direction.WEST) {
                                if (portalFacing == Direction.NORTH || portalFacing == Direction.SOUTH) {
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = 0;
                                }
                            }
                        }
                    }

                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                            if (otherPortalVertFacing == Direction.NORTH || otherPortalVertFacing == Direction.SOUTH) {
                                if (portalVertFacing == Direction.EAST || portalVertFacing == Direction.WEST) {
                                    double storedX = teleportXOffset;
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = -storedX;
                                }
                            }

                            if (otherPortalVertFacing == Direction.EAST || otherPortalVertFacing == Direction.WEST) {
                                if (portalVertFacing == Direction.NORTH || portalVertFacing == Direction.SOUTH) {
                                    double storedZ = teleportZOffset;
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = -storedZ;
                                }
                            }
                            if (portalVertFacing != null)
                                if (otherPortalVertFacing == portalVertFacing.getOpposite()) {
                                    teleportZOffset = -teleportZOffset;
                                    teleportXOffset = -teleportXOffset;
                                }
                        }
                    }

                    if (portalFacing == Direction.NORTH || portalFacing == Direction.SOUTH) {
                        if (otherDirec == Direction.EAST || otherDirec == Direction.WEST) {
                            teleportZOffset = -teleportXOffset;
                            teleportXOffset = 0;
                        }
                    }

                    if (portalFacing == Direction.EAST || portalFacing == Direction.WEST) {
                        if (otherDirec == Direction.NORTH || otherDirec == Direction.SOUTH) {
                            teleportXOffset = -teleportZOffset;
                            teleportZOffset = 0;
                        }
                    }
                    Vec3d entityEyePos = this.getEyePos();
                    if (portalFacing.getUnitVector().getX() < 0) {
                        if (entityEyePos.getX() + entityVelocity.getX() > portal.getPos().getX()) {
                            performTeleport(thisentity, portal, teleportXOffset, teleportYOffset, teleportZOffset, otherDirec, entityVelocity);
                            break;
                        }
                    }
                    if (portalFacing.getUnitVector().getY() < 0) {
                        if (entityEyePos.getY() + entityVelocity.getY() > portal.getPos().getY()) {
                            performTeleport(thisentity, portal, teleportXOffset, teleportYOffset, teleportZOffset, otherDirec, entityVelocity);
                            break;
                        }
                    }
                    if (portalFacing.getUnitVector().getZ() < 0) {
                        if (entityEyePos.getZ() + entityVelocity.getZ() > portal.getPos().getZ()) {
                            performTeleport(thisentity, portal, teleportXOffset, teleportYOffset, teleportZOffset, otherDirec, entityVelocity);
                            break;
                        }
                    }


                    if (portalFacing.getUnitVector().getX() > 0) {
                        if (entityEyePos.getX() + entityVelocity.getX() < portal.getPos().getX()) {
                            performTeleport(thisentity, portal, teleportXOffset, teleportYOffset, teleportZOffset, otherDirec, entityVelocity);
                            break;
                        }
                    }
                    if (portalFacing.getUnitVector().getY() > 0) {
                        if (entityEyePos.getY() + entityVelocity.getY() < portal.getPos().getY()) {
                            if (this.world.isClient && thisentity.isMainPlayer()) {
                                var byteBuf = PacketByteBufs.create();
                                byteBuf.writeVarInt(portal.getId());
                                byteBuf.writeDouble(teleportXOffset);
                                byteBuf.writeDouble(teleportYOffset);
                                byteBuf.writeDouble(teleportZOffset);
                                byteBuf.writeFloat(this.getYaw());
                                byteBuf.writeFloat(thisentity.getPitch());

                                if (!(otherDirec.getUnitVector().getY() < 0)) {
                                    if(!CalledValues.getWasInfiniteFalling(thisentity)) {
                                        double velocity = 0;
                                        double fall = ((EntityAttachments) this).getMaxFallHeight();
                                        fall = fall - portal.getPos().y;
                                        if (fall < 0) {
                                            velocity = entityVelocity.y;
                                        } else {
                                            if (thisentity.hasNoDrag()) {
                                                velocity = -Math.sqrt(2 * .08 * (fall)) + .0402;
                                            } else {
                                                velocity = (-Math.sqrt(2 * .08 * (fall)) + .0402) / .98;
                                            }

                                        }
                                        entityVelocity = new Vec3d(entityVelocity.x, velocity, entityVelocity.z);
                                    }else{
                                        CalledValues.setWasInfiniteFalling(thisentity,false);
                                    }
                                }else{
                                    CalledValues.setWasInfiniteFalling(thisentity,true);
                                }

                                byteBuf.writeDouble(entityVelocity.x);
                                byteBuf.writeDouble(entityVelocity.y);
                                byteBuf.writeDouble(entityVelocity.z);
                                byteBuf.writeBoolean(wasInfiniteFall);

                                NetworkingSafetyWrapper.sendFromClient("portalpacket", byteBuf);
                                //storedYaw = yawValue;
                                //COMEHERE

                                //((EntityAttachments) thisentity).setTeleportVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                this.setVelocity(0,0,0);
                            }
                            break;
                        }
                    }
                    if (portalFacing.getUnitVector().getZ() > 0) {
                        if (entityEyePos.getZ() + entityVelocity.getZ() < portal.getPos().getZ()) {
                            performTeleport(thisentity, portal, teleportXOffset, teleportYOffset, teleportZOffset, otherDirec, entityVelocity);
                            break;
                        }
                    }

                }
            }if(this.world.isClient && CalledValues.getHasTeleportationHappened(this)){
                this.setVelocity(0,0,0);
                break;
            }
        }

        if (world.isClient && CalledValues.getHasTeleportationHappened(thisentity)) {
            var byteBuf = PacketByteBufs.create();
            NetworkingSafetyWrapper.sendFromClient("clientteleportupdate", byteBuf);
            CalledValues.setHasTeleportationHappened(thisentity, false);
            ((EntityAttachments) thisentity).setMaxFallHeight(-99999999);
            //this.setYaw(storedYaw);
            this.setVelocity(CalledValues.getVelocityUpdateAfterTeleport(thisentity));
        }
    }

    private void performTeleport(
            PlayerEntity thisEntity,
            ExperimentalPortal portal,
            double teleportXOffset,
            double teleportYOffset,
            double teleportZOffset,
            Direction otherDir,
            Vec3d entityVelocity
    ) {
        if (this.world.isClient && thisEntity.isMainPlayer()) {
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeVarInt(portal.getId());
            byteBuf.writeDouble(teleportXOffset);
            byteBuf.writeDouble(teleportYOffset);
            byteBuf.writeDouble(teleportZOffset);
            byteBuf.writeFloat(this.getYaw());
            byteBuf.writeFloat(thisEntity.getPitch());
            byteBuf.writeDouble(entityVelocity.x);
            byteBuf.writeDouble(entityVelocity.y);
            byteBuf.writeDouble(entityVelocity.z);
            byteBuf.writeBoolean(wasInfiniteFall);
            NetworkingSafetyWrapper.sendFromClient("portalpacket", byteBuf);
            this.setVelocity(0,0,0);
            //storedYaw = yawValue;
            //((EntityAttachments) thisEntity).setTeleportVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDir));

        }
    }


    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D"))
    public void portalCubed$dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<@Nullable ItemEntity> cir) {
        if (!this.world.isClient && stack.getItem().equals(PortalCubedItems.PORTAL_GUN)) {
            NbtCompound tag = stack.getOrCreateNbt();
            NbtCompound portalsTag = tag.getCompound(world.getRegistryKey().toString());
            ExperimentalPortal portalHolder;
            if (portalsTag.contains(("Left") + "Portal")) {
                portalHolder = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Left") + "Portal"));
                if (portalHolder != null) {
                    portalHolder.kill();
                }
            }
            if (portalsTag.contains(("Right") + "Portal")) {
                portalHolder = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Right") + "Portal"));
                if (portalHolder != null) {
                    portalHolder.kill();
                }
            }
        }
    }
}




