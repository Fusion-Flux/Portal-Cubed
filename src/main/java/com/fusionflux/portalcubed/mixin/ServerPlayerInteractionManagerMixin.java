package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow @Final protected ServerPlayerEntity player;

    @Redirect(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 0))
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck4(Vec3d from, Vec3d to) {
        return CrossPortalInteraction.interactionDistance(player, ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE, to);
    }

}
