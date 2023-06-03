package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerPlayerGameMode.class, priority = 1001)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow @Final protected ServerPlayer player;

    @Redirect(
        method = "handleBlockBreakAction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck4(Vec3 from, Vec3 to) {
        return CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, to);
    }
}
