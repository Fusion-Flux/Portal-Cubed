package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerPlayerGameMode.class, priority = 1001)
public abstract class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;

    @WrapOperation(
        method = "handleBlockBreakAction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck4(Vec3 instance, Vec3 to, Operation<Double> original) {
        final double distance = CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, to);
        return distance == Double.NEGATIVE_INFINITY ? original.call(instance, to) : distance;
    }
}
