package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.HeldItemRendererExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin implements HeldItemRendererExt {
    @Shadow @Final private Minecraft minecraft;
    private boolean isHoldingInvisible;
    private boolean handFaker;

    @Inject(
        method = "renderArmWithItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            ordinal = 1
        )
    )
    private void shakeGun(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        final long time = Util.getMillis() - PortalCubedClient.shakeStart;
        if (time > 440) return;
        matrices.mulPose(Vector3f.ZP.rotationDegrees((float)Math.sin(time / 35.0) * 6));
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.PUTFIELD,
            ordinal = 1
        )
    )
    private void isHoldingInvisible(CallbackInfo ci) {
        assert minecraft.player != null;
        isHoldingInvisible =
            !minecraft.player.getMainHandItem().is(PortalCubedItems.HOLDS_OBJECT) &&
                PortalCubedComponents.HOLDER_COMPONENT.get(minecraft.player).entityBeingHeld() != null;
        handFaker = false;
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void noHandObject(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (hand == InteractionHand.MAIN_HAND && isHoldingInvisible) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void noHandHud(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm, CallbackInfo ci) {
        if (PortalCubedClient.isPortalHudMode()) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.GETFIELD,
            ordinal = 1
        )
    )
    private ItemStack handFaker(ItemInHandRenderer instance, Operation<ItemStack> original) {
        return handFaker ? null : original.call(instance);
    }

    @Override
    public void startHandFaker() {
        handFaker = true;
    }
}
