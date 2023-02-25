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
    public static Vec3d rotateVelocity(Vec3d velocity, Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> velocity.multiply(-1, 1, -1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(-1, 1, -1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(-1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                default -> velocity;
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(-1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                default -> velocity;
            };
            case UP -> switch (exitDirection) {
                case UP -> velocity.multiply(-1, -1, -1);
                case NORTH -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                case SOUTH -> new Vec3d(-velocity.getX(), -velocity.getZ(), -velocity.getY());
                case EAST -> new Vec3d(-velocity.getY(), -velocity.getX(), -velocity.getZ());
                case WEST -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                default -> velocity;
            };
            case DOWN -> switch (exitDirection) {
                case DOWN -> velocity.multiply(-1, -1, -1);
                case NORTH -> new Vec3d(-velocity.getX(), -velocity.getZ(), -velocity.getY());
                case SOUTH -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                case EAST -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                case WEST -> new Vec3d(-velocity.getY(), -velocity.getX(), -velocity.getZ());
                default -> velocity;
            };
        };
    }


    public static Vec3d rotatePosition(Vec3d velocity, double entityHeight, Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> velocity.multiply(-1, 1, 1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), -velocity.getZ(), -velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), velocity.getZ(), -velocity.getY());
                default -> velocity.multiply(1, 1, -1);
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(-1, 1, 1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity.multiply(1, 1, -1);
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                default -> velocity.multiply(-1, 1, 1);
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                default -> velocity.multiply(-1, 1, 1);
            };
            case UP -> switch (exitDirection) {
                case UP -> velocity;
                case NORTH -> new Vec3d(velocity.getX(), entityHeight, -velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), entityHeight, velocity.getY());
                case EAST -> new Vec3d(velocity.getY(), entityHeight, velocity.getZ());
                case WEST -> new Vec3d(-velocity.getY(), entityHeight, velocity.getZ());
                default -> velocity.multiply(1, -1, 1);
            };
            case DOWN -> switch (exitDirection) {
                case DOWN -> velocity;
                case NORTH -> new Vec3d(velocity.getX(), entityHeight, velocity.getY());
                case SOUTH -> new Vec3d(velocity.getX(), entityHeight, -velocity.getY());
                case EAST -> new Vec3d(-velocity.getY(), entityHeight, velocity.getZ());
                case WEST -> new Vec3d(velocity.getY(), entityHeight, velocity.getZ());
                default -> velocity.multiply(1, -1, 1);
            };
        };
    }

    public static final AdvancedEntityRaycast.TransformInfo PORTAL_RAYCAST_TRANSFORM = new AdvancedEntityRaycast.TransformInfo(
        e -> e instanceof ExperimentalPortal,
        (context, blockHit, entityHit) -> {
            final ExperimentalPortal portal = (ExperimentalPortal)entityHit.getEntity();
            if (!portal.getActive()) return null;
            final double distance = context.getStart().distanceTo(context.getEnd());
            final Vec3d offset = blockHit.getPos().subtract(context.getStart());

            final Direction facing = portal.getFacingDirection();
            final Direction otherFacing = Direction.fromVector(new BlockPos(portal.getOtherFacing()));
            final Vec3d newOffset = rotateVelocity(offset, facing, otherFacing)
                .normalize()
                .multiply(distance - offset.length());
            final Vec3d hitRelative = entityHit.getPos().subtract(portal.getOriginPos())
                .withAxis(facing.getAxis(), 0);
            final Vec3d newRel = rotateVelocity(hitRelative, facing, otherFacing);
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
