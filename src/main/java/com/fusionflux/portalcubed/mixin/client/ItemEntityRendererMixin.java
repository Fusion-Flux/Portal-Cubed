package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD")
    )
    private void laysOnFloor(
        ItemEntity itemEntity,
        float f,
        float g,
        PoseStack matrixStack,
        MultiBufferSource vertexConsumerProvider,
        int i,
        CallbackInfo ci,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor
    ) {
        if (!PortalCubedConfig.staticPortalItemDrops) {
            laysOnFloor.set(false);
            return;
        }
        final ItemStack stack = itemEntity.getItem();
        laysOnFloor.set(stack.getCount() == 1 && stack.is(PortalCubedItems.LAYS_ON_FLOOR));
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            ordinal = 0
        )
    )
    private void dontTranslate(
        PoseStack instance,
        float x,
        float y,
        float z,
        Operation<Void> original,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor,
        @Local(ordinal = 2) float yOffset
    ) {
        original.call(instance, x, laysOnFloor.get() ? 0.25 * yOffset : y, z);
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;getSpin(F)F"
        )
    )
    private float dontRotate(
        ItemEntity instance,
        float tickDelta,
        Operation<Float> original,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor
    ) {
        return laysOnFloor.get() ? instance.bobOffs : original.call(instance, tickDelta);
    }
}
