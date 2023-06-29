package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.BaseGel;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {
    @Shadow protected boolean jumping;

    @Shadow protected abstract float getFrictionInfluencedSpeed(float slipperiness);

    @Shadow protected abstract Vec3 handleOnClimbable(Vec3 motion);

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow public abstract ItemStack getItemInHand(InteractionHand hand);

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean isJumping() {
        return jumping;
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double cfg(double original) {
        if (((EntityExt)this).cfg()) {
            return 0;
        }
        return original;
    }


    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("HEAD"), cancellable = true)
    public void handleFrictionAndCalculateMovement(Vec3 movementInput, float slipperiness, CallbackInfoReturnable<Vec3> cir) {
        if (((EntityExt) this).isInFunnel()) {
            this.updateVelocityCustom(this.getFrictionInfluencedSpeed(slipperiness), movementInput);
            this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
            this.move(MoverType.SELF, this.getDeltaMovement());
            Vec3 vec3d = this.getDeltaMovement();
            cir.setReturnValue(vec3d);
        }
    }

    @Unique
    public void updateVelocityCustom(float speed, Vec3 movementInput) {
        Vec3 vec3d = movementInputToVelocityCustom(movementInput, speed, this.getYRot(), this.getXRot());
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d));
    }

    @Unique
    private static Vec3 movementInputToVelocityCustom(Vec3 movementInput, float speed, float yaw, float pitch) {
        double d = movementInput.lengthSqr();
        if (d < 1.0E-7) {
            return Vec3.ZERO;
        } else {
            Vec3 vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).scale(speed);
            float f = Mth.sin(yaw * 0.017453292F);
            float g = Mth.cos(yaw * 0.017453292F);
            float x = Mth.sin(pitch * 0.017453292F);
            return new Vec3(vec3d.x * (double)g - vec3d.z * (double)f, -vec3d.z * (double)x, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
    private void crowbarSwoosh(InteractionHand hand, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof Player player && getItemInHand(hand).is(PortalCubedItems.CROWBAR)) {
            level().playSound(
                player,
                player.getX(), player.getY(), player.getZ(),
                PortalCubedSounds.CROWBAR_SWOOSH_EVENT, SoundSource.PLAYERS,
                0.7f, 1f
            );
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (getItemBySlot(EquipmentSlot.FEET).is(PortalCubedItems.LONG_FALL_BOOTS)) {
            cir.setReturnValue(false);
            return;
        }
        if (!isSuppressingBounce()) {
            final AABB boundingBox = getBoundingBox();
            for (BlockPos pos : BlockPos.betweenClosed(
                (int)Math.floor(boundingBox.minX),
                (int)Math.floor(boundingBox.minY),
                (int)Math.floor(boundingBox.minZ),
                (int)Math.ceil(boundingBox.maxX),
                (int)Math.ceil(boundingBox.maxY),
                (int)Math.ceil(boundingBox.maxZ)
            )) {
                final BlockState state = level().getBlockState(pos);
                if (state.is(PortalCubedBlocks.REPULSION_GEL) && BaseGel.collides(this, pos, state)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
