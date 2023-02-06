package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.gui.ExpressionFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {
    @Shadow protected boolean jumping;

    @Unique
    private VelocityHelperBlockEntity velocityHelper;
    @Unique
    private long velocityHelperStartTime;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isJumping() {
        return jumping;
    }

    @Override
    public void collidedWithVelocityHelper(VelocityHelperBlockEntity block) {
        //noinspection ConstantValue
        if ((Object) this instanceof PlayerEntity player) {
            if (!player.isMainPlayer()) return;
        } else if (world.isClient) return;
        if (block.getDestination() == null) return;
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
        ).subtract(getPos()));
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
