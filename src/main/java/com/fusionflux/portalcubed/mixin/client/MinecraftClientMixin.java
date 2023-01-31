package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.items.*;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    public MinecraftClientMixin() {
        throw new AssertionError(PortalCubed.MOD_ID + "'s MinecraftClientMixin dummy constructor was called, something is very wrong here!");
    }

    /**
     * Prevents block breaking on player left click while holding a portal gun as left click functionality is replaced.
     *
     * @author Platymemo
     */
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void portalCubed$onHandleBlockBreaking(boolean isKeyPressed, CallbackInfo ci) {
        assert this.player != null;
        if (this.player.isHolding(PortalCubedItems.PORTAL_GUN) ||  this.player.isHolding(PortalCubedItems.PAINT_GUN) ||  this.player.isHolding(PortalCubedItems.PORTAL_GUN_PRIMARY) ||  this.player.isHolding(PortalCubedItems.PORTAL_GUN_SECONDARY) ) {
            ci.cancel();
        }
    }

    /**
     * Prevents block breaking on player left click while holding a portal gun as left click functionality is replaced.
     * Instead, sends a custom packet representing the left click with a portal gun to the server.
     *
     * @author Platymemo
     */
    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void portalCubed$onDoAttack(CallbackInfoReturnable<Boolean> cir) {
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
                sendLeftClickPacket.accept(Hand.MAIN_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            } else if (offHand instanceof PortalGun) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            }

            if (mainHand instanceof PortalGun || offHand instanceof PortalGun) {
                cir.cancel();
            }


            if (mainHand instanceof PortalGunPrimary) {
                sendLeftClickPacket.accept(Hand.MAIN_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            } else if (offHand instanceof PortalGunPrimary) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            }

            if (mainHand instanceof PortalGunPrimary || offHand instanceof PortalGunPrimary) {
                cir.cancel();
            }


            if (mainHand instanceof PortalGunSecondary) {
                sendLeftClickPacket.accept(Hand.MAIN_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            } else if (offHand instanceof PortalGunSecondary) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            }

            if (mainHand instanceof PortalGunSecondary || offHand instanceof PortalGunSecondary) {
                cir.cancel();
            }


            if (mainHand instanceof PaintGun) {
                sendLeftClickPacket.accept(Hand.MAIN_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            } else if (offHand instanceof PaintGun) {
                sendLeftClickPacket.accept(Hand.OFF_HAND, PortalCubedServerPackets.PORTAL_LEFT_CLICK);
            }

            if (mainHand instanceof PaintGun || offHand instanceof PaintGun) {
                cir.cancel();
            }
        }
    }
}