package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.gui.ExpressionFieldWidget;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

    @Shadow public abstract boolean canMoveVoluntarily();

    @Shadow protected abstract float getMovementSpeed(float slipperiness);

    @Shadow protected abstract Vec3d applyClimbingSpeed(Vec3d motion);

    @Shadow public abstract void setNoDrag(boolean noDrag);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Unique
    private VelocityHelperBlockEntity velocityHelper;
    @Unique
    private long velocityHelperStartTime;
    @Unique
    private Vec3d velocityHelperOffset;

    public LivingEntityMixin(EntityType<?> type, World world) {
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


    @Inject(method = "handleFrictionAndCalculateMovement", at = @At("HEAD"), cancellable = true)
    public void handleFrictionAndCalculateMovement(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        if (((EntityAttachments) this).isInFunnel()) {
            this.updateVelocityCustom(this.getMovementSpeed(slipperiness), movementInput);
            this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
            this.move(MovementType.SELF, this.getVelocity());
            Vec3d vec3d = this.getVelocity();
            cir.setReturnValue(vec3d);
        }
    }

    public void updateVelocityCustom(float speed, Vec3d movementInput) {
        Vec3d vec3d = movementInputToVelocityCustom(movementInput, speed, this.getYaw(), this.getPitch());
        this.setVelocity(this.getVelocity().add(vec3d));
    }

    private static Vec3d movementInputToVelocityCustom(Vec3d movementInput, float speed, float yaw, float pitch) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        } else {
            Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
            float f = MathHelper.sin(yaw * 0.017453292F);
            float g = MathHelper.cos(yaw * 0.017453292F);
            float x = MathHelper.sin(pitch * 0.017453292F);
            return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, -vec3d.z * (double)x, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }


    @Override
    public void collidedWithVelocityHelper(VelocityHelperBlockEntity block) {
        if (!canMoveVoluntarily() || block.getDestination() == null) return;
        if (velocityHelper != null && block.getPos() != null && block.getPos().equals(velocityHelper.getPos())) return;
        final Expression condition = block.getCondition();
        condition.setVariable("x", getVelocity().x);
        condition.setVariable("y", getVelocity().y);
        condition.setVariable("z", getVelocity().z);
        try {
            if (condition.evaluate() == 0) return;
        } catch (RuntimeException e) {
            logVHWarning("condition", e);
            return;
        }
        velocityHelper = block;
        velocityHelperStartTime = world.getTime();
        velocityHelperOffset = Vec3d.ofCenter(block.getPos()).subtract(getPos());
    }

    @Override
    public void collidedWithCatapult(CatapultBlockEntity block) {
        if (!canMoveVoluntarily()) return;
        final double relH = block.getRelH();
        final double relY = block.getRelY();
        final double angle = block.getAngle();
        final double speed = GeneralUtil.calculateVelocity(relH, relY, angle, -0.08);
        if (!Double.isFinite(speed)) return;
        setNoDrag(getEquippedStack(EquipmentSlot.FEET).isOf(PortalCubedItems.LONG_FALL_BOOTS));
        setVelocity(block.getLaunchDir().multiply(Math.min(speed, 10)));
        velocityDirty = true;
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickVelocityHelper(CallbackInfo ci) {
        if (velocityHelper == null || velocityHelper.getDestination() == null) {
            velocityHelper = null;
            return;
        }
        double progress = (world.getTime() - velocityHelperStartTime) / (double)velocityHelper.getFlightDuration();
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
            useProgress = MathHelper.clamp(curve.evaluate(), 0, 1);
        } catch (RuntimeException e) {
            logVHWarning("curve", e);
            return;
        }
        assert velocityHelper.getDestination() != null;
        setVelocity(new Vec3d(
            MathHelper.lerp(useProgress, velocityHelper.getPos().getX() + 0.5, velocityHelper.getDestination().getX() + 0.5),
            MathHelper.lerp(useProgress, velocityHelper.getPos().getY() + 0.5, velocityHelper.getDestination().getY() + 0.5),
            MathHelper.lerp(useProgress, velocityHelper.getPos().getZ() + 0.5, velocityHelper.getDestination().getZ() + 0.5)
        ).subtract(getPos()).subtract(velocityHelperOffset));
    }

    private void logVHWarning(String type, RuntimeException e) {
        //noinspection ConstantValue
        if ((Object)this instanceof PlayerEntity && world.isClient) {
            logVHWarningToChat(type, e);
        }
        PortalCubed.LOGGER.info("{} at {}", getVHWarning(type).getString(), velocityHelper.getPos(), e);
    }

    @ClientOnly
    private void logVHWarningToChat(String type, RuntimeException e) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(getVHWarning(type));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
            Text.literal(ExpressionFieldWidget.cleanError(e)).formatted(Formatting.RED)
        );
    }

    private Text getVHWarning(String type) {
        return Text.translatable(
            "portalcubed.velocity_helper.failed_expression",
            Text.translatable("portalcubed.velocity_helper." + type + "_expression")
        ).formatted(Formatting.RED);
    }
}
