package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
    private double portalCubed$crossPortalInteractionAntiCheatFix1(Vec3d from, Vec3d to) {
        return portalCubed$portalCompatibleSquaredDistanceTo(from, to, ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE);
    }

    private double portalCubed$portalCompatibleSquaredDistanceTo(Vec3d from, Vec3d to, double maxRayDistance) {
        final var end = player.getRotationVector().multiply(maxRayDistance/maxRayDistance);
        final var box = player.getBoundingBox().stretch(end).expand(1d);
        final var hit = ProjectileUtil.raycast(player, player.getEyePos(), player.getEyePos().add(end), box, e -> e instanceof ExperimentalPortal, maxRayDistance);
        if (hit != null && hit.getEntity() instanceof ExperimentalPortal portal && portal.getDestination().isPresent()) {
            return portal.getDestination().get().add(0, player.getEyeY(), 0).squaredDistanceTo(to);
        }
        return from.squaredDistanceTo(to);
    }

}
