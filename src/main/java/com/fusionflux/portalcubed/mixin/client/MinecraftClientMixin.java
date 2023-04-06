package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PaintGun;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;
    @Shadow public ClientPlayerEntity player;
    @Shadow private void doItemUse() {
        throw new UnsupportedOperationException();
    }
    @Shadow private boolean doAttack() {
        throw new UnsupportedOperationException();
    }

    @Shadow @Nullable public HitResult crosshairTarget;

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 1))
    private void portalCubed$stopPortalSpamming(MinecraftClient self) {
        if (!(player.getMainHandStack().getItem() instanceof PortalGun)) {
            doItemUse();
        }
    }

    @Inject(method = "handleInputEvents", at = @At("RETURN"))
    private void portalCubed$allowConstantAttack(CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof PaintGun && options.attackKey.isPressed()) {
            doAttack();
        }
    }

    @WrapOperation(
        method = "handleInputEvents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBind;wasPressed()Z"
        )
    )
    private boolean portalHudDisableKeys(KeyBind instance, Operation<Boolean> original) {
        final boolean result = original.call(instance);
        if (!PortalCubedClient.isPortalHudMode()) {
            return result;
        }
        for (final KeyBind bind : options.hotbarKeys) {
            if (instance == bind) {
                return false;
            }
        }
        if (
            instance == options.inventoryKey ||
                instance == options.swapHandsKey ||
                instance == options.dropKey
        ) {
            return false;
        }
        return result;
    }

    @WrapOperation(
        method = "doAttack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            ordinal = 0
        )
    )
    private BlockState crowbarAttack(ClientWorld instance, BlockPos pos, Operation<BlockState> original) {
        if (player.getMainHandStack().isOf(PortalCubedItems.CROWBAR)) {
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockHitResult((BlockHitResult)crosshairTarget);
            ClientPlayNetworking.send(PortalCubedServerPackets.CROWBAR_ATTACK, buf);
            return Blocks.AIR.getDefaultState();
        }
        return original.call(instance, pos);
    }

    @WrapOperation(
        method = {"doAttack", "handleBlockBreaking"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            ordinal = 0
        )
    )
    private BlockState noMineInPortalHud(ClientWorld instance, BlockPos pos, Operation<BlockState> original) {
        return PortalCubedClient.isPortalHudMode() ? Blocks.AIR.getDefaultState() : original.call(instance, pos);
    }

}
