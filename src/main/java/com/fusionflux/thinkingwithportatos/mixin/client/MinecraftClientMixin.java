package com.fusionflux.thinkingwithportatos.mixin.client;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    public MinecraftClientMixin() {
        throw new AssertionError(ThinkingWithPortatos.MODID + "'s MinecraftClientMixin dummy constructor was called, something is very wrong here!");
    }

    @Shadow
    @Nullable
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    /**
     * Prevents block breaking on player left click while holding a portal gun as left click functionality is replaced.
     *
     * @author Platymemo
     */
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(boolean isKeyPressed, CallbackInfo ci) {
        assert this.player != null;
        if (this.player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN) || this.player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2)) {
            ci.cancel();
        }
    }

    /**
     * Prevents block breaking on player left click while holding a portal gun as left click functionality is replaced.
     * Instead, sends a custom packet representing the left click with a portal gun to the server.
     *
     * @author Platymemo
     */
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfo ci) {
        // Probably an unnecessary check as there can't be an attack without a player.
        if (player != null) {

            // Need to get the hand, but there's probably a cooler looking method than this.
            Hand hand = null;
            if (player.getMainHandStack().getItem() == ThinkingWithPortatosItems.PORTAL_GUN || player.getMainHandStack().getItem() == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2) {
                hand = Hand.MAIN_HAND;
            } else if (player.getOffHandStack().getItem() == ThinkingWithPortatosItems.PORTAL_GUN || player.getOffHandStack().getItem() == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2) {
                hand = Hand.OFF_HAND;
            }

            // If hand != null then there must be a portal gun in hand,
            // so we can send the packet and cancel the rest of the doAttack method.
            if (hand != null) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeEnumConstant(hand);
                Packet<?> packet = ClientPlayNetworking.createC2SPacket(new Identifier(ThinkingWithPortatos.MODID, "ptl_lft_click"), buf);
                Objects.requireNonNull(this.getNetworkHandler()).sendPacket(packet);
                ci.cancel();
            }
        }
    }
}