package com.fusionflux.thinkingwithportatos.mixin.client;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.packet.ThinkingWithPortatosServerPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;

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

            // If hand != null then there must be a portal gun in hand,
            // so we can send the packet and cancel the rest of the doAttack method.
            Arrays.stream(Hand.values()).filter(hand -> {
                Item item = player.getStackInHand(hand).getItem();
                return item == ThinkingWithPortatosItems.PORTAL_GUN || item == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2;
            }).findFirst().ifPresent(hand -> {
                PacketByteBuf buf = PacketByteBufs.create()
                        .writeEnumConstant(hand);
                ClientPlayNetworking.send(ThinkingWithPortatosServerPackets.PORTAL_LEFT_CLICK, buf);
                ci.cancel();
            });
        }
    }
}