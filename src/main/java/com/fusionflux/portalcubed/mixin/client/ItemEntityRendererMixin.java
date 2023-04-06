package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @Inject(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD")
    )
    private void laysOnFloor(
        ItemEntity itemEntity,
        float f,
        float g,
        MatrixStack matrixStack,
        VertexConsumerProvider vertexConsumerProvider,
        int i,
        CallbackInfo ci,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor
    ) {
        if (!PortalCubedConfig.staticPortalItemDrops) {
            laysOnFloor.set(false);
            return;
        }
        final ItemStack stack = itemEntity.getStack();
        laysOnFloor.set(stack.getCount() == 1 && stack.isIn(PortalCubedItems.LAYS_ON_FLOOR));
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
            ordinal = 0
        )
    )
    private void dontTranslate(
        MatrixStack instance,
        double x,
        double y,
        double z,
        Operation<Float> original,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor,
        @Local(ordinal = 2) float yOffset
    ) {
        original.call(instance, x, laysOnFloor.get() ? 0.25 * yOffset : y, z);
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ItemEntity;getRotation(F)F"
        )
    )
    private float dontRotate(
        ItemEntity instance,
        float tickDelta,
        Operation<Float> original,
        @Share("laysOnFloor") LocalBooleanRef laysOnFloor
    ) {
        return laysOnFloor.get() ? instance.uniqueOffset : original.call(instance, tickDelta);
    }
}
