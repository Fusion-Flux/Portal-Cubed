package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.MinecraftExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PaintGun;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftExt {

    @Shadow @Final public Options options;
    @Shadow public LocalPlayer player;
    @Shadow
    protected abstract void startUseItem();
    @Shadow
    protected abstract boolean startAttack();

    @Shadow @Nullable public HitResult hitResult;

    @Mutable
    @Shadow @Final private RenderBuffers renderBuffers;

    @Redirect(
        method = "handleKeybinds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;startUseItem()V",
            ordinal = 1
        )
    )
    private void portalCubed$stopPortalSpamming(Minecraft self) {
        if (!(player.getMainHandItem().getItem() instanceof PortalGun)) {
            startUseItem();
        }
    }

    @Inject(method = "handleKeybinds", at = @At("RETURN"))
    private void portalCubed$allowConstantAttack(CallbackInfo ci) {
        if (player.getMainHandItem().getItem() instanceof PaintGun && options.keyAttack.isDown()) {
            startAttack();
        }
    }

    @WrapOperation(
        method = "handleKeybinds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"
        )
    )
    private boolean portalHudDisableKeys(KeyMapping instance, Operation<Boolean> original) {
        final boolean result = original.call(instance);
        if (!PortalCubedClient.isPortalHudMode()) {
            return result;
        }
        for (final KeyMapping bind : options.keyHotbarSlots) {
            if (instance == bind) {
                return false;
            }
        }
        if (
            instance == options.keyInventory ||
                instance == options.keySwapOffhand ||
                instance == options.keyDrop
        ) {
            return false;
        }
        return result;
    }

    @WrapOperation(
        method = {"startAttack", "continueAttack"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            ordinal = 0
        )
    )
    private BlockState noMineInPortalHud(ClientLevel instance, BlockPos pos, Operation<BlockState> original) {
        return PortalCubedClient.isPortalHudMode() || player.getMainHandItem().is(PortalCubedItems.CROWBAR)
            ? Blocks.AIR.defaultBlockState() : original.call(instance, pos);
    }

    @WrapOperation(
        method = "startAttack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            ordinal = 0
        )
    )
    private BlockState crowbarAttack(ClientLevel instance, BlockPos pos, Operation<BlockState> original) {
        if (player.getMainHandItem().is(PortalCubedItems.CROWBAR)) {
            final FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBlockHitResult((BlockHitResult)hitResult);
            ClientPlayNetworking.send(PortalCubedServerPackets.CROWBAR_ATTACK, buf);
        }
        return original.call(instance, pos);
    }

    @Override
    public void setRenderBuffers(RenderBuffers buffers) {
        renderBuffers = buffers;
    }
}
