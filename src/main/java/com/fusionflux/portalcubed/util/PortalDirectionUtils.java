package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;

public class PortalDirectionUtils {
    public static Vec3d rotateVector(ExperimentalPortal portal, Vec3d vector) {
        Direction portalFacing = portal.getFacingDirection();
        Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());

        IPQuaternion rotationW = IPQuaternion.getRotationBetween(portal.getAxisW().orElseThrow().multiply(-1), portal.getOtherAxisW(), (portal.getAxisH().orElseThrow()));
        IPQuaternion rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getAxisW().orElseThrow().multiply(-1));

        if(portalFacing == Direction.UP || portalFacing == Direction.DOWN){
            if(otherDirec.equals(portalFacing)) {
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

}
