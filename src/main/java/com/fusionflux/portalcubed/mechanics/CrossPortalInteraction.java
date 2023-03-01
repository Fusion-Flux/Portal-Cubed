package com.fusionflux.portalcubed.mechanics;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class CrossPortalInteraction {

    private CrossPortalInteraction() { }

    @Nullable
    public static BlockHitResult blockInteractionRaycast(@NotNull World world, @NotNull RaycastContext context) {
        final var result = PortalDirectionUtils.raycast(world, context);
        final var finalHit = result.finalHit();
        ((AdvancedRaycastResultHolder) finalHit).setResult(Optional.of(result));
        return finalHit;
    }

    public static double blockInteractionDistance(@NotNull Entity originEntity, @NotNull Vec3d originPos, @NotNull Vec3d endPos, @NotNull Vec3d regularInteractionPos) {
        final var rays = PortalDirectionUtils.raycast(originEntity.world, new RaycastContext(originPos, endPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, originEntity)).rays();
        if (rays.size() > 1) {
            var length = 0.0;
            for (AdvancedEntityRaycast.Result.Ray ray : rays) {
                System.out.println("RAY:");
                System.out.println(length);
                length += ray.start().distanceTo(ray.end());
                System.out.println(length);
            }
            length *= length;
            System.out.println("FINAL:");
            System.out.println(length);
            return length;
        }
        return originPos.squaredDistanceTo(regularInteractionPos);
    }

    public static double blockInteractionDistance(@NotNull PlayerEntity player, @NotNull Vec3d regularInteractionPos) {
        return blockInteractionDistance(player, player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(ServerPlayNetworkHandler.MAX_INTERACTION_DISTANCE)), regularInteractionPos);
    }

}
