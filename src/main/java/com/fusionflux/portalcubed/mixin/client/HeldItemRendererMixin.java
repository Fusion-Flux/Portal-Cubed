package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.HeldItemRendererExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin implements HeldItemRendererExt {
    @Shadow @Final private MinecraftClient client;
    private boolean isHoldingInvisible;
    private boolean handFaker;

    @Inject(
        method = "renderFirstPersonItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            ordinal = 1
        )
    )
    private void shakeGun(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        final long time = Util.getMeasuringTimeMs() - PortalCubedClient.shakeStart;
        if (time > 440) return;
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)Math.sin(time / 35.0) * 6));
    }

    @Inject(
        method = "updateHeldItems",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;mainHand:Lnet/minecraft/item/ItemStack;",
            opcode = Opcodes.PUTFIELD,
            ordinal = 1
        )
    )
    private void isHoldingInvisible(CallbackInfo ci) {
        assert client.player != null;
        isHoldingInvisible =
            !client.player.getMainHandStack().isIn(PortalCubedItems.HOLDS_OBJECT) &&
                PortalCubedComponents.HOLDER_COMPONENT.get(client.player).entityBeingHeld() != null;
        handFaker = false;
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void noHandObject(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (hand == Hand.MAIN_HAND && isHoldingInvisible) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"), cancellable = true)
    private void noHandHud(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "updateHeldItems",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;mainHand:Lnet/minecraft/item/ItemStack;",
            opcode = Opcodes.GETFIELD,
            ordinal = 1
        )
    )
    private ItemStack handFaker(HeldItemRenderer instance, Operation<ItemStack> original) {
        return handFaker ? null : original.call(instance);
    }

    @Override
    public void startHandFaker() {
        handFaker = true;
    }
}
