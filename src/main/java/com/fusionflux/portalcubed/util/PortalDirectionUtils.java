package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class PortalDirectionUtils {
    public static Vec3d rotateVector(ExperimentalPortal portal, Vec3d vector) {
        Direction portalFacing = portal.getFacingDirection();
        Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());
        Direction portalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));

        IPQuaternion rotationW = IPQuaternion.getRotationBetween(portal.getAxisW().orElseThrow().multiply(-1), portal.getOtherAxisW(), (portal.getAxisH().orElseThrow()));
        IPQuaternion rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getAxisW().orElseThrow().multiply(-1));

        if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
            if (otherDirec.equals(portalFacing) || (portalVertFacing != otherDirec && portalVertFacing != otherDirec.getOpposite())) {
                rotationW = IPQuaternion.getRotationBetween(portal.getNormal().multiply(-1), portal.getOtherNormal(), (portal.getAxisH().orElseThrow()));
                rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getNormal().multiply(-1));
            }
        }

        vector = (rotationH.rotate(rotationW.rotate(vector)));
        return vector;
    }

    public static final AdvancedEntityRaycast.TransformInfo PORTAL_RAYCAST_TRANSFORM = new AdvancedEntityRaycast.TransformInfo(
        e -> e instanceof ExperimentalPortal,
        (context, blockHit, entityHit) -> {
            final ExperimentalPortal portal = (ExperimentalPortal)entityHit.getEntity();
            if (!portal.getActive()) return null;
            final double distance = context.getStart().distanceTo(context.getEnd());
            final Vec3d offset = blockHit.getPos().subtract(context.getStart());

            final Direction facing = portal.getFacingDirection();
            final Vec3d newOffset = rotateVector(portal, offset)
                .normalize()
                .multiply(distance - offset.length());
            final Vec3d hitRelative = entityHit.getPos().subtract(portal.getOriginPos())
                .withAxis(facing.getAxis(), 0);
            final Vec3d newRel = rotateVector(portal, hitRelative);
            final Vec3d newStart = portal.getDestination().orElseThrow().add(newRel);
            return new AdvancedEntityRaycast.TransformResult(
                blockHit.getPos(),
                AdvancedEntityRaycast.withStartEnd(context, newStart, newStart.add(newOffset)),
                portal.getLinkedPortalUUID()
                    .flatMap(id -> Optional.ofNullable(((Accessors)portal.getWorld()).getEntity(id)))
                    .map(Set::of)
                    .orElse(Set.of())
            );
        }
    );

    public static AdvancedEntityRaycast.Result raycast(World world, RaycastContext context) {
        return AdvancedEntityRaycast.raycast(world, context, PORTAL_RAYCAST_TRANSFORM);
    }

    /**
     * @param originEntity The entity responsible for the raycast
     * @return New vector after transformation. If null, there was no portal between {@code startPos} and {@code endPos}.
     *         Usually this is interpreted as a return value of {@code endPos}, but that's not a requirement.
     */
    @Nullable
    public static Vec3d simpleTransformPassingVector(Entity originEntity, Vec3d startPos, Vec3d endPos) {
        final EntityHitResult hit = ProjectileUtil.raycast(
            originEntity, startPos, endPos,
            new Box(startPos, endPos).expand(1),
            e -> e instanceof ExperimentalPortal,
            startPos.squaredDistanceTo(endPos)
        );
        if (hit == null) return null;
        final ExperimentalPortal portal = (ExperimentalPortal)hit.getEntity();
        final Direction facing = portal.getFacingDirection();
        final double yOffset = endPos.distanceTo(startPos) - hit.getPos().distanceTo(startPos);
        final Vec3d hitRelative = PortalDirectionUtils.rotateVector(
            portal,
            hit.getPos()
                .subtract(portal.getOriginPos())
                .withAxis(facing.getAxis(), facing.getDirection().offset() * -1 * yOffset)
        );
        return portal.getDestination().orElseThrow().add(hitRelative);
    }

}
