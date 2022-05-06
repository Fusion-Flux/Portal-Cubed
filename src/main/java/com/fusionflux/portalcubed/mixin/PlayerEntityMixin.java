package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EntityAttachments {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        throw new AssertionError(
                PortalCubed.MODID + "'s PlayerEntityMixin dummy constructor was called, " +
                        "something is very wrong here!"
        );
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract boolean isSwimming();

    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract float getMovementSpeed();


    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack5 = this.getEquippedStack(EquipmentSlot.FEET);
        if (damageSource == DamageSource.FALL && (itemStack5.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS))) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3d uhhhhhhh(Vec3d travelVectorOriginal) {
        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        if (!this.isOnGround() && !this.abilities.flying && !this.isFallFlying() && itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.world.getBlockState(this.getBlockPos()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.world.getBlockState(new BlockPos(this.getBlockPos().getX(),this.getBlockPos().getY()+1,this.getBlockPos().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {
            double mathval = 1;
            double horizontalvelocity = Math.abs(this.getVelocity().x) + Math.abs(this.getVelocity().z);
            if (horizontalvelocity / 0.01783440120041885 > 1) {
                mathval = horizontalvelocity / 0.01783440120041885;
            }
            travelVectorOriginal = new Vec3d(travelVectorOriginal.x / mathval, travelVectorOriginal.y, travelVectorOriginal.z / mathval);
            this.airStrafingSpeed = .04f;
        }
    return travelVectorOriginal;
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        this.setNoDrag((!this.isOnGround() && !this.abilities.flying && !this.isFallFlying() && itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.world.getBlockState(this.getBlockPos()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.world.getBlockState(new BlockPos(this.getBlockPos().getX(),this.getBlockPos().getY()+1,this.getBlockPos().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)));

        if (!world.isClient) {

            //if (CollisionHelper.isCollidingWithAnyPortal(this) && !recentlyTouchedPortal) {
            //    //world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
            //    recentlyTouchedPortal = true;
            //}
            //if (!CollisionHelper.isCollidingWithAnyPortal(this) && recentlyTouchedPortal) {
            //    //world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
            //    recentlyTouchedPortal = false;
            //}

            if (this.isHolding(PortalCubedItems.PORTAL_GUN)) {
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.PORTAL_AMBIANT_EVENT, SoundCategory.NEUTRAL, .001F, 1F);
            }
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<@Nullable ItemEntity> cir) {
        if (!this.world.isClient && stack.getItem().equals(PortalCubedItems.PORTAL_GUN)) {
            NbtCompound tag = stack.getOrCreateNbt();
            NbtCompound portalsTag = tag.getCompound(world.getRegistryKey().toString());
            ExperimentalPortal portalholder;
            if (portalsTag.contains(("Left") + "Portal")) {
                portalholder = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Left") + "Portal"));
                if (portalholder != null) {
                    portalholder.kill();
                }
            }
            if (portalsTag.contains(("Right") + "Portal")) {
                portalholder = (ExperimentalPortal) ((ServerWorld) world).getEntity(portalsTag.getUuid(("Right") + "Portal"));
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



