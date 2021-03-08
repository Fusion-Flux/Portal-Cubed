package com.fusionflux.fluxtech.mixin;

import com.fusionflux.fluxtech.accessor.AttackUseCase;
import com.fusionflux.fluxtech.items.FluxTechItems;
import com.fusionflux.fluxtech.items.MinecraftClientMethods;
import com.fusionflux.fluxtech.items.PortalGun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;




@Mixin(value = MinecraftClient.class, priority = 1500)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;
    @Shadow
    public HitResult crosshairTarget;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    public int attackCooldown;

    public MinecraftClientMixin() {
    }

    @Shadow
    protected abstract void doItemPick();

   /* @Inject(
            method = {"handleBlockBreaking"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
            )},
            cancellable = true
    )
    private void onHandleBlockBreaking(boolean isKeyPressed, CallbackInfo ci) {
        if (MinecraftClientMethods.isPointingToPortal()) {
            MinecraftClientMethods.myHandleBlockBreaking(isKeyPressed);
            ci.cancel();
        }

    }*/

    @Shadow private static MinecraftClient instance;

    @Inject(
            method = {"doAttack"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onDoAttack(CallbackInfo ci) {
        assert player != null;
        if (player.isHolding(FluxTechItems.PORTAL_GUN)) {
            ((AttackUseCase)instance).setAttackUse(true);
        }
        ci.cancel();

    }

   /* @Inject(
            method = {"doItemUse"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
            )},
            cancellable = true
    )
    private void onDoItemUse(CallbackInfo ci) {
        if (MinecraftClientMethods.isPointingToPortal()) {
            MinecraftClientMethods.myItemUse(Hand.MAIN_HAND);
            ci.cancel();
        }

    }*/

}