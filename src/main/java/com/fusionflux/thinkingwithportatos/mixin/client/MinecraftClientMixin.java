package com.fusionflux.thinkingwithportatos.mixin.client;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.GravityGun;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.packet.ThinkingWithPortatosServerPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

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
        if (this.player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN)) {
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
        if (this.player != null) {
            Item mainHand = this.player.getMainHandStack().getItem();
            Item offHand = this.player.getOffHandStack().getItem();

            BiConsumer<Hand, Identifier> sendLeftClickPacket = (hand, packetId) -> {
                // If hand != null then there must be a portal gun in hand,
                // so we can send the packet and cancel the rest of the doAttack method.
                if (hand != null) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeEnumConstant(hand);
                    ClientPlayNetworking.send(packetId, buf);
                }
            };

            // Note: Doesn't allow user to fire two portal guns at once
            if (mainHand instanceof PortalGun) {
                sendLeftClickPacket.accept(Hand.MAIN_HAND, ThinkingWithPortatosServerPackets.PORTAL_LEFT_CLICK);
            } else if (offHand instanceof PortalGun) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, ThinkingWithPortatosServerPackets.PORTAL_LEFT_CLICK);
            } else if (mainHand instanceof GravityGun) {
                sendLeftClickPacket.accept(Hand.MAIN_HAND, ThinkingWithPortatosServerPackets.GRAVITY_LEFT_CLICK);
            } else if (offHand instanceof GravityGun) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, ThinkingWithPortatosServerPackets.GRAVITY_LEFT_CLICK);
            }

            if (mainHand instanceof PortalGun || offHand instanceof PortalGun || mainHand instanceof GravityGun || offHand instanceof GravityGun) {
                ci.cancel();
            }
        }
    }
}