package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.listeners.CustomCollisionView;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract ServerPlayerEntity getPlayer();

    @ModifyVariable(method = "isPlayerNotCollidingWithBlocks(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/Box;)Z", at = @At("STORE"), ordinal = 0)
    private Iterable<VoxelShape> isPlayerNotCollidingWithBlocks(Iterable<VoxelShape> shapes) {
        VoxelShape portalBox = CalledValues.getPortalCutout(this.getPlayer());
        if (portalBox != VoxelShapes.empty()) {
            // Would take in the world value from the code, but I guess I cant
            return (((CustomCollisionView) this.player.getWorld()).getPortalCollisions(this.getPlayer(), this.getPlayer().getBoundingBox().contract(1.0E-5F), portalBox));
        }

        return shapes;
    }

    @Redirect(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 0))
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck1(Vec3d from, Vec3d to) {
        return CrossPortalInteraction.interactionDistance(player, ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE, to);
    }

    @Redirect(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;squaredDistanceTo(DDD)D", ordinal = 0))
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck2(ServerPlayerEntity self, double x, double y, double z) {
        return CrossPortalInteraction.interactionDistance(player, 64, new Vec3d(x, y, z));
    }

    @Redirect(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 0))
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck3(Entity self, Vec3d to) {
        return CrossPortalInteraction.interactionDistance(player, ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE, to);
    }

}
