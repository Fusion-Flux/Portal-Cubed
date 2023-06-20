package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.BaseGel;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.gui.ExpressionFieldWidget;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiApi;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.objecthunter.exp4j.Expression;
import org.quiltmc.loader.api.minecraft.ClientOnly;
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

    @Shadow public abstract void setDiscardFriction(boolean noDrag);

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow public abstract ItemStack getItemInHand(InteractionHand hand);

    @Unique
    private VelocityHelperBlockEntity velocityHelper;
    @Unique
    private long velocityHelperStartTime;
    @Unique
    private Vec3 velocityHelperOffset;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean isJumping() {
        return jumping;
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double cfg(double original) {
        if (((EntityAttachments)this).cfg()) {
            return 0;
        }
        return original;
    }


    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("HEAD"), cancellable = true)
    public void handleFrictionAndCalculateMovement(Vec3 movementInput, float slipperiness, CallbackInfoReturnable<Vec3> cir) {
        if (((EntityAttachments) this).isInFunnel()) {
            this.updateVelocityCustom(this.getFrictionInfluencedSpeed(slipperiness), movementInput);
            this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
            this.move(MoverType.SELF, this.getDeltaMovement());
            Vec3 vec3d = this.getDeltaMovement();
            cir.setReturnValue(vec3d);
        }
    }

    public void updateVelocityCustom(float speed, Vec3 movementInput) {
        Vec3 vec3d = movementInputToVelocityCustom(movementInput, speed, this.getYRot(), this.getXRot());
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d));
    }

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


    @Override
    public void collidedWithVelocityHelper(VelocityHelperBlockEntity block) {
        if (!isEffectiveAi() || block.getDestination() == null) return;
        if (velocityHelper != null && block.getBlockPos() != null && block.getBlockPos().equals(velocityHelper.getBlockPos())) return;
        final Expression condition = block.getCondition();
        condition.setVariable("x", getDeltaMovement().x);
        condition.setVariable("y", getDeltaMovement().y);
        condition.setVariable("z", getDeltaMovement().z);
        try {
            if (condition.evaluate() == 0) return;
        } catch (RuntimeException e) {
            logVHWarning("condition", e);
            return;
        }
        velocityHelper = block;
        velocityHelperStartTime = level.getGameTime();
        velocityHelperOffset = Vec3.atCenterOf(block.getBlockPos()).subtract(position());
    }

    @Override
    public void collidedWithCatapult(CatapultBlockEntity block) {
        if (!isEffectiveAi()) return;
        final double relH = block.getRelH(position().x, position().z);
        final double relY = block.getRelY(position().y);
        final double angle = block.getAngle();
        final double speed = GeneralUtil.calculateVelocity(relH, relY, angle, -0.08 * PehkuiApi.INSTANCE.getFallingScale(this));
        if (!Double.isFinite(speed)) return;
        //noinspection ConstantValue
        if ((Object)this instanceof Player) {
            setDiscardFriction(getItemBySlot(EquipmentSlot.FEET).is(PortalCubedItems.LONG_FALL_BOOTS));
        }
        RayonIntegration.INSTANCE.setVelocity(this, block.getLaunchDir(position().x, position().z).scale(Math.min(speed, 10)));
        hasImpulse = true;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void tickVelocityHelper(CallbackInfo ci) {
        if (velocityHelper == null || velocityHelper.getDestination() == null) {
            velocityHelper = null;
            return;
        }
        double progress = (level.getGameTime() - velocityHelperStartTime) / (double)velocityHelper.getFlightDuration();
        if (progress >= 1.0) {
            velocityHelper = null;
            return;
        }
        if (progress < 0) {
            progress = 0;
        }
        final Expression curve = velocityHelper.getInterpolationCurve();
        curve.setVariable("x", progress);
        final double useProgress;
        try {
            useProgress = Mth.clamp(curve.evaluate(), 0, 1);
        } catch (RuntimeException e) {
            logVHWarning("curve", e);
            return;
        }
        assert velocityHelper.getDestination() != null;
        setDeltaMovement(new Vec3(
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getX() + 0.5, velocityHelper.getDestination().getX() + 0.5),
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getY() + 0.5, velocityHelper.getDestination().getY() + 0.5),
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getZ() + 0.5, velocityHelper.getDestination().getZ() + 0.5)
        ).subtract(position()).subtract(velocityHelperOffset));
    }

    private void logVHWarning(String type, RuntimeException e) {
        //noinspection ConstantValue
        if ((Object)this instanceof Player && level.isClientSide) {
            logVHWarningToChat(type, e);
        }
        PortalCubed.LOGGER.info("{} at {}", getVHWarning(type).getString(), velocityHelper.getBlockPos(), e);
    }

    @ClientOnly
    private void logVHWarningToChat(String type, RuntimeException e) {
        Minecraft.getInstance().gui.getChat().addMessage(getVHWarning(type));
        Minecraft.getInstance().gui.getChat().addMessage(
            Component.literal(ExpressionFieldWidget.cleanError(e)).withStyle(ChatFormatting.RED)
        );
    }

    private Component getVHWarning(String type) {
        return Component.translatable(
            "portalcubed.velocity_helper.failed_expression",
            Component.translatable("portalcubed.velocity_helper." + type + "_expression")
        ).withStyle(ChatFormatting.RED);
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
    private void crowbarSwoosh(InteractionHand hand, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof Player player && getItemInHand(hand).is(PortalCubedItems.CROWBAR)) {
            level.playSound(
                player,
                player.getX(), player.getY(), player.getZ(),
                PortalCubedSounds.CROWBAR_SWOOSH_EVENT, SoundSource.PLAYERS,
                0.7f, 1f
            );
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void repulsionGelNoFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        final AABB boundingBox = getBoundingBox();
        for (BlockPos pos : BlockPos.betweenClosed(
            (int)Math.floor(boundingBox.minX),
            (int)Math.floor(boundingBox.minY),
            (int)Math.floor(boundingBox.minZ),
            (int)Math.ceil(boundingBox.maxX),
            (int)Math.ceil(boundingBox.maxY),
            (int)Math.ceil(boundingBox.maxZ)
        )) {
            final BlockState state = level.getBlockState(pos);
            if (state.is(PortalCubedBlocks.REPULSION_GEL) && BaseGel.collides(this, pos, state)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
