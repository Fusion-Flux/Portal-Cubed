package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    private Vec3d storeVelocity1;
    @Unique
    private Vec3d storeVelocity2;
    private boolean recentlyTouchedPortal;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        throw new AssertionError(
                ThinkingWithPortatos.MODID + "'s PlayerEntityMixin dummy constructor was called, " +
                        "something is very wrong here!"
        );
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract boolean isSwimming();



    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack5 = this.getEquippedStack(EquipmentSlot.FEET);
        if (damageSource == DamageSource.FALL && (itemStack5.getItem().equals(ThinkingWithPortatosItems.LONG_FALL_BOOTS))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getFallSound", at = @At("HEAD"), cancellable = true)
    protected void getFallSound(int distance, CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        if (itemFeet.getItem().equals(ThinkingWithPortatosItems.LONG_FALL_BOOTS)) {
            cir.setReturnValue(SoundEvents.BLOCK_WOOL_FALL);
        }
    }



    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {



        /*List<Portal> globalPortals = (List<Portal>) McHelper.getNearbyPortals(((PlayerEntity) (Object) this),50);
        Vec3d expand = this.getVelocity().multiply(2);
        Box streachedBB = this.getBoundingBox().stretch(expand);

        for (Portal globalPortal : globalPortals) {
            if (streachedBB.intersects(globalPortal.getBoundingBox())) {
                this.setVelocity(this.getVelocity().multiply(5, 5, 5));
            }
        }*/

        /*double reduceGravity = .06666666666;
        double gravityBalancer = 1;
            if (this.isTouchingWater()) {
                gravityBalancer = gravityBalancer * .02;
                if (this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                    gravityBalancer = gravityBalancer * .01;
                }
            }
if(!this.isFallFlying()) {
    if (!this.isSwimming()) {
        this.setVelocity(this.getVelocity().add(0, reduceGravity * gravityBalancer, 0));
    }
}*/
        if (!world.isClient) {

            if (CollisionHelper.isCollidingWithAnyPortal(this) && !recentlyTouchedPortal) {
                //world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                recentlyTouchedPortal = true;
            }
            if (!CollisionHelper.isCollidingWithAnyPortal(this) && recentlyTouchedPortal) {
                //world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                recentlyTouchedPortal = false;
            }

            if (this.isHolding(ThinkingWithPortatosItems.PORTAL_GUN)) {
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.PORTAL_AMBIANT_EVENT, SoundCategory.NEUTRAL, .001F, 1F);
            }
        }
        storeVelocity2 = storeVelocity1;
        storeVelocity1 = this.getVelocity();

        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        if (storeVelocity2 != null && storeVelocity1 != null) {
            if (itemFeet.getItem().equals(ThinkingWithPortatosItems.LONG_FALL_BOOTS)) {
                if (!this.isOnGround()) {
                    this.setVelocity(this.getVelocity().x * 1.045, this.getVelocity().y, this.getVelocity().z * 1.045);
                }
            }
        }

        if (this.isOnGround()) {
            this.setNoDrag(false);
            //this.setVelocity(this.getVelocity().x * 1.045, this.getVelocity().y, this.getVelocity().z * 1.035);
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<@Nullable ItemEntity> cir) {
        if (!this.world.isClient && stack.getItem().equals(ThinkingWithPortatosItems.PORTAL_GUN)) {
            NbtCompound tag = stack.getOrCreateNbt();
            NbtCompound portalsTag = tag.getCompound(world.getRegistryKey().toString());
            CustomPortalEntity portalholder;
            if (portalsTag.contains(("Left") + "Portal")) {
                portalholder = (CustomPortalEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Left") + "Portal"));
                if (portalholder != null) {
                    portalholder.kill();
                }
            }
            if (portalsTag.contains(("Right") + "Portal")) {
                portalholder = (CustomPortalEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Right") + "Portal"));
                if (portalholder != null) {
                    portalholder.kill();
                }
            }
            PortalPlaceholderEntity portalOutline;
            if (portalsTag.contains(("Left") + "Background")) {
                portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Left") + "Background"));
                if (portalOutline != null) {
                    portalOutline.kill();
                }
            }
            if (portalsTag.contains(("Right") + "Background")) {
                portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Right") + "Background"));
                if (portalOutline != null) {
                    portalOutline.kill();
                }
            }
        }
    }

}




