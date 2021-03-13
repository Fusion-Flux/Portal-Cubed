package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    private Vec3d storeVelocity1;
    @Unique
    private Vec3d storeVelocity2;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        throw new AssertionError(
                ThinkingWithPortatos.MOD_ID + "'s PlayerEntityMixin dummy constructor was called, " +
                        "something is very wrong here!"
        );
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

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


        storeVelocity2 = storeVelocity1;
        storeVelocity1 = this.getVelocity();

        ItemStack itemFeet = this.getEquippedStack(EquipmentSlot.FEET);
        if (storeVelocity2 != null && storeVelocity1 != null){
            if (itemFeet.getItem().equals(ThinkingWithPortatosItems.LONG_FALL_BOOTS)) {
                if (!this.isOnGround()) {
                    if(Math.abs(storeVelocity1.x)<Math.abs(storeVelocity2.x)||Math.abs(storeVelocity1.z)<Math.abs(storeVelocity2.z)) {
                        storeVelocity2 = new Vec3d(storeVelocity2.x, this.getVelocity().y, storeVelocity2.z);
                        this.setVelocity(storeVelocity2);
                    }else{
                        storeVelocity1 = new Vec3d(storeVelocity1.x, this.getVelocity().y, storeVelocity1.z);
                        this.setVelocity(storeVelocity1);
                    }

                }
            }
    }
    }

}




