package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerPlayerInteractionManager.class, priority = 1001)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow @Final protected ServerPlayerEntity player;

    @Redirect(
        method = "processBlockBreakingAction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck4(Vec3d from, Vec3d to) {
        return CrossPortalInteraction.interactionDistance(player, ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE, to);
    }

    @WrapOperation(
        method = "interactBlock",
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
