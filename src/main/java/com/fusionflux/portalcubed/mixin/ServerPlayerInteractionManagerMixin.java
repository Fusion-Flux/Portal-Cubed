package com.fusionflux.portalcubed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Vec3d;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow public ServerPlayerEntity player;

    @Redirect(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 0))
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck3(Vec3d from, Vec3d to) {
        return CrossPortalInteraction.blockInteractionDistance(player, to);
    }

}
