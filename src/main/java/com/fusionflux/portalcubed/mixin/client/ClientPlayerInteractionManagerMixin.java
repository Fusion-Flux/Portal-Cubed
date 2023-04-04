package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @WrapOperation(
        method = "m_wiqrsdhj",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
        )
    )
    private ActionResult noPlaceInPortalHud(BlockState instance, World world, PlayerEntity player, Hand hand, BlockHitResult hit, Operation<ActionResult> original) {
        if (PortalCubedClient.isPortalHudMode()) {
            return ActionResult.PASS;
        }
        return original.call(instance, world, player, hand, hit);
    }
}
