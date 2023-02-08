package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.fusionflux.portalcubed.util.PortalVelocityHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EntityAttachments {
    @Unique
    private boolean cfg;

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

    boolean isTeleporting;

    boolean hasPreviouslyTeleported;

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
        if(isTeleporting){
            travelVectorOriginal = Vec3d.ZERO;
        }

    return travelVectorOriginal;
    }

    private boolean enableNoDrag2;

    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        PlayerEntity thisEntity = ((PlayerEntity) (Object) this);

        if (world.isClient && CalledValues.getHasTeleportationHappened(thisEntity)) {
            var byteBuf = PacketByteBufs.create();
            NetworkingSafetyWrapper.sendFromClient("client_teleport_update", byteBuf);
            CalledValues.setHasTeleportationHappened(thisEntity, false);
            ((EntityAttachments) thisEntity).setMaxFallHeight(-99999999);
            isTeleporting = false;
            this.setVelocity(CalledValues.getVelocityUpdateAfterTeleport(thisEntity));
        }

        Vec3d entityVelocity = this.getVelocity();

        Box portalCheckBox = getBoundingBox();
        if (!this.world.isClient) {
            portalCheckBox = portalCheckBox.expand(10);
        } else {
            portalCheckBox = portalCheckBox.stretch(entityVelocity);
        }
        List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);
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
                this.setVelocity(this.getVelocity().add(0,.081d,0));
            }
        }

        if (!PortalCubedClient.allowCfg || !isSneaking()) {
            cfg = false;
        }
    }



    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        PlayerEntity thisEntity = ((PlayerEntity) (Object) this);

        Vec3d entityVelocity = this.getVelocity();

        Box portalCheckBox = getBoundingBox();

        portalCheckBox = portalCheckBox.stretch(entityVelocity).stretch(entityVelocity.multiply(-1));


        List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);
        ExperimentalPortal portal = null;
        double portalDistance = -1;
        for (ExperimentalPortal portalCheck : list) {
            if (portalDistance == -1 || portalCheck.getBoundingBox().getCenter().subtract(thisEntity.getBoundingBox().getCenter()).length() < portalDistance) {
                portalDistance = portalCheck.getBoundingBox().getCenter().subtract(thisEntity.getBoundingBox().getCenter()).length();
                portal = portalCheck;
            }
        }
        if (portal != null) {
            if (this.canUsePortals() && portal.getActive() && this.world.isClient && !CalledValues.getHasTeleportationHappened(thisEntity) && !isTeleporting) {
                Direction portalFacing = portal.getFacingDirection();
                Direction portalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));

                Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());
                Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(portal.getOtherAxisH().x, portal.getOtherAxisH().y, portal.getOtherAxisH().z));

                if (otherDirec != null) {
                    double teleportYOffset = (portal.getBoundingBox().getCenter().y - thisEntity.getPos().y);
                    double teleportXOffset = -(portal.getPos().x - thisEntity.getPos().x);
                    double teleportZOffset = -(portal.getPos().z - thisEntity.getPos().z);

                    if(portalFacing == Direction.UP){
                        if(otherDirec == Direction.DOWN){
                            teleportYOffset = (portal.getBoundingBox().getCenter().y - thisEntity.getPos().y);
                        }else {
                            teleportYOffset = -(portal.getPos().y - thisEntity.getPos().y);
                        }
                    }

                    Vec3d rotatedOffsets = new Vec3d(teleportXOffset,teleportYOffset,teleportZOffset);

                    if(portalFacing == Direction.UP || portalFacing ==Direction.DOWN) {
                        if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                            rotatedOffsets = PortalVelocityHelper.rotateVelocity(rotatedOffsets, portalVertFacing, otherDirec);
                        }
                    }

                    rotatedOffsets = PortalVelocityHelper.rotateVelocity(rotatedOffsets, portalFacing, otherDirec);

                    if(otherDirec == Direction.UP || otherDirec ==Direction.DOWN) {
                        if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                            rotatedOffsets = PortalVelocityHelper.rotateVelocity(rotatedOffsets, portalFacing, otherPortalVertFacing);
                        }
                    }

                    if(portalFacing == Direction.UP || portalFacing ==Direction.DOWN) {
                        if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                            rotatedOffsets = PortalVelocityHelper.rotateVelocity(rotatedOffsets, portalVertFacing, otherPortalVertFacing);
                        }
                    }


                    teleportXOffset = rotatedOffsets.getX();
                    teleportYOffset = rotatedOffsets.getY();
                    teleportZOffset = rotatedOffsets.getZ();


                    Vec3d entityEyePos = this.getEyePos();
                    if (portalFacing.getUnitVector().getX() < 0) {
                        if (entityEyePos.getX() + entityVelocity.getX() > portal.getPos().getX() && entityVelocity.getX() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);

                        }
                    }
                    if (portalFacing.getUnitVector().getY() < 0) {
                        if (entityEyePos.getY() + entityVelocity.getY() > portal.getPos().getY() && entityVelocity.getY() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);

                        }
                    }
                    if (portalFacing.getUnitVector().getZ() < 0) {
                        if (entityEyePos.getZ() + entityVelocity.getZ() > portal.getPos().getZ() && entityVelocity.getZ() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);

                        }
                    }


                    if (portalFacing.getUnitVector().getX() > 0) {
                        if (entityEyePos.getX() + entityVelocity.getX() < portal.getPos().getX() && entityVelocity.getX() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);

                        }
                    }
                    if (portalFacing.getUnitVector().getY() > 0) {
                        if (entityEyePos.getY() + entityVelocity.getY() < portal.getPos().getY() && entityVelocity.getY() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            if (portalFacing.getOpposite() == otherDirec) {
                                CalledValues.setWasInfiniteFalling(thisEntity, true);
                            }
                            if(!hasPreviouslyTeleported){
                                hasPreviouslyTeleported = true;
                                performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity.add(0,-0.00316799700927733,0));
                            }else {
                                performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);
                            }
                        }
                    }
                    if (portalFacing.getUnitVector().getZ() > 0) {
                        if (entityEyePos.getZ() + entityVelocity.getZ() < portal.getPos().getZ() && entityVelocity.getZ() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                            performTeleport(thisEntity, portal, teleportXOffset, teleportYOffset, teleportZOffset, entityVelocity);

                        }
                    }

                }
            }
            if (this.world.isClient && CalledValues.getHasTeleportationHappened(this)) {
                this.setVelocity(0, 0, 0);
            }
        }

        if (world.isClient && CalledValues.getHasTeleportationHappened(thisEntity)) {
            var byteBuf = PacketByteBufs.create();
            NetworkingSafetyWrapper.sendFromClient("client_teleport_update", byteBuf);
            CalledValues.setHasTeleportationHappened(thisEntity, false);
            ((EntityAttachments) thisEntity).setMaxFallHeight(-99999999);
            isTeleporting = false;
            this.setVelocity(CalledValues.getVelocityUpdateAfterTeleport(thisEntity));
        }

        if(thisEntity.isOnGround() && hasPreviouslyTeleported){
            hasPreviouslyTeleported = false;
        }
    }

    private void performTeleport(
            PlayerEntity thisEntity,
            ExperimentalPortal portal,
            double teleportXOffset,
            double teleportYOffset,
            double teleportZOffset,
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
            byteBuf.writeBoolean(CalledValues.getWasInfiniteFalling(thisEntity));
            NetworkingSafetyWrapper.sendFromClient("use_portal", byteBuf);
            isTeleporting = true;
            this.setVelocity(0,0,0);

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

    @Override
    public boolean cfg() {
        return cfg;
    }

    @Override
    public void setCFG() {
        cfg = true;
    }
}




