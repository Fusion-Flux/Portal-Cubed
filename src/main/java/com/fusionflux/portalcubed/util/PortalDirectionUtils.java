package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.entity.Portal;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class PortalDirectionUtils {
    public static Vec3 rotateVector(Portal portal, Vec3 vector) {
        return portal.getTransformQuat().rotate(vector, false);
    }

    public static final AdvancedEntityRaycast.TransformInfo PORTAL_RAYCAST_TRANSFORM = new AdvancedEntityRaycast.TransformInfo(
        e -> e instanceof Portal,
        (context, blockHit, entityHit) -> {
            final Portal portal = (Portal)entityHit.getEntity();
            if (!portal.getActive()) return null;
            final double distance = context.getFrom().distanceTo(context.getTo());
            final Vec3 offset = blockHit.getLocation().subtract(context.getFrom());

            final Direction facing = portal.getFacingDirection();
            final Vec3 newOffset = rotateVector(portal, offset)
                .normalize()
                .scale(distance - offset.length());
            final Vec3 hitRelative = entityHit.getLocation().subtract(portal.getOriginPos())
                .with(facing.getAxis(), 0);
            final Vec3 newRel = rotateVector(portal, hitRelative);
            final Vec3 newStart = portal.getDestination().orElseThrow().add(newRel);
            return new AdvancedEntityRaycast.TransformResult(
                blockHit.getLocation(),
                AdvancedEntityRaycast.withStartEnd(context, newStart, newStart.add(newOffset)),
                portal.getLinkedPortalUUID()
                    .flatMap(id -> Optional.ofNullable(((LevelExt)portal.getLevel()).getEntity(id)))
                    .map(Set::of)
                    .orElse(Set.of())
            );
        }
    );

    public static AdvancedEntityRaycast.Result raycast(Level world, ClipContext context) {
        return AdvancedEntityRaycast.raycast(world, context, PORTAL_RAYCAST_TRANSFORM);
    }

    /**
     * @param originEntity The entity responsible for the raycast
     * @return New vector after transformation. If null, there was no portal between {@code startPos} and {@code endPos}.
     *         Usually this is interpreted as a return value of {@code endPos}, but that's not a requirement.
     */
    @Nullable
    public static Pair<Vec3, Portal> simpleTransformPassingVector(Entity originEntity, Vec3 startPos, Vec3 endPos, Predicate<Portal> portalPredicate) {
        final EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            originEntity, startPos, endPos,
            new AABB(startPos, endPos).inflate(1),
            e -> e instanceof Portal portal && portal.getActive() && portalPredicate.test(portal),
            startPos.distanceToSqr(endPos)
        );
        if (hit == null) return null;
        final Portal portal = (Portal)hit.getEntity();
        final Direction facing = portal.getFacingDirection();
        final double yOffset = endPos.distanceTo(startPos) - hit.getLocation().distanceTo(startPos);
        final Vec3 hitRelative = PortalDirectionUtils.rotateVector(
            portal,
            hit.getLocation()
                .subtract(portal.getOriginPos())
                .with(facing.getAxis(), facing.getAxisDirection().getStep() * -1 * yOffset)
        );
        return Pair.of(portal.getDestination().orElseThrow().add(hitRelative), portal);
    }

}
