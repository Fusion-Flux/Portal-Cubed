package com.fusionflux.fluxtech.mixin;

import com.fusionflux.fluxtech.FluxTech;
import com.fusionflux.fluxtech.accessor.AttackUseCase;
import com.fusionflux.fluxtech.items.FluxTechItems;
import com.fusionflux.fluxtech.items.MinecraftClientMethods;
import com.fusionflux.fluxtech.items.PortalGun;
import com.qouteall.immersive_portals.network.McRemoteProcedureCall;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    public MinecraftClientMixin() {
        throw new AssertionError(FluxTech.MOD_ID + "'s MinecraftClientMixin dummy constructor was called, something is very wrong here!");
    }

   @Inject(
            method = "handleBlockBreaking",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHandleBlockBreaking(boolean isKeyPressed, CallbackInfo ci) {
        if (this.player.isHolding(FluxTechItems.PORTAL_GUN)) {
            ci.cancel();
        }

    }


    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfo ci) {
        assert player != null;
        Hand hand = null;
        if (player.getMainHandStack().getItem() == FluxTechItems.PORTAL_GUN) {
            hand = Hand.MAIN_HAND;
        } else if (player.getOffHandStack().getItem() == FluxTechItems.PORTAL_GUN) {
            hand = Hand.OFF_HAND;
        }
        if (hand != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeEnumConstant(hand);
            Packet<?> packet = ClientPlayNetworking.createC2SPacket(new Identifier(FluxTech.MOD_ID, "portal_left_click"), buf);
            this.getNetworkHandler().sendPacket(packet);
            ci.cancel();
        }
    }
}