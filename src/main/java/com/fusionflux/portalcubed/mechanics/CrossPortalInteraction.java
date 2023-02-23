package com.fusionflux.portalcubed.mechanics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class CrossPortalInteraction {

    private CrossPortalInteraction() { }

    private static final double MAGIC_CORRECTING_NUMBER = .0015;

    @Nullable
    public static BlockHitResult blockInteractionRaycast(@NotNull World world, @NotNull RaycastContext context) {
        BlockHitResult finalHit = null;
        final var rays = PortalDirectionUtils.raycast(world, context).rays();
        if (rays.size() == 1) return (BlockHitResult) rays.get(rays.size() - 1).hit();

        for (AdvancedEntityRaycast.Result.Ray ray : rays) {
            var start = ray.start();
            var end = ray.end();
            start = start.subtract(end.subtract(start).multiply(MAGIC_CORRECTING_NUMBER));

            finalHit = world.raycast(AdvancedEntityRaycast.withStartEnd(context, start, end));
            if (finalHit.getType() != BlockHitResult.Type.MISS) break;
        }

        return finalHit;
    }

}
