package com.fusionflux.portalcubed.mechanics;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class CrossPortalInteraction {

	private CrossPortalInteraction() { }

	public static BlockHitResult blockInteractionRaycast(@NotNull Level world, @NotNull ClipContext context) {
		final var result = PortalDirectionUtils.raycast(world, context);
		final var finalHit = (BlockHitResult)result.finalHit();
		((AdvancedRaycastResultHolder) finalHit).setResult(Optional.of(result));
		return finalHit;
	}

	public static double interactionDistance(@NotNull Entity originEntity, @NotNull Vec3 originPos, @NotNull Vec3 endPos, @NotNull Vec3 regularInteractionPos) {
		final var rays = PortalDirectionUtils.raycast(originEntity.level(), new ClipContext(originPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, originEntity)).rays();
		if (rays.size() > 1) {
			var distance = 0.0;
			for (AdvancedEntityRaycast.Result.Ray ray : rays) {
				distance += ray.start().distanceTo(ray.end());
			}
			distance *= distance;
			return distance;
		}
		return Double.NEGATIVE_INFINITY;
	}

	public static double interactionDistance(@NotNull Player player, double maxDistance, @NotNull Vec3 regularInteractionPos) {
		return interactionDistance(player, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(maxDistance)), regularInteractionPos);
	}

}
