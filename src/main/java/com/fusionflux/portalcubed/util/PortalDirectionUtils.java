package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.mixin.RaycastContextAccessor;
import com.google.common.base.Suppliers;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PortalDirectionUtils {
    public static Vec3d rotateVelocity(Vec3d velocity, Direction entryDirection, Direction exitDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (exitDirection) {
                case NORTH -> velocity.multiply(-1, 1, -1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(-1, 1, -1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(),- velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(),- velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                default -> velocity;
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(-1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                default -> velocity;
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(-1, 1, -1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
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
                case NORTH -> velocity.multiply(-1,1,1);
                case EAST -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case WEST -> new Vec3d(velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), -velocity.getZ(), -velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), velocity.getZ(), -velocity.getY());
                default -> velocity.multiply(1, 1, -1);
            };
            case SOUTH -> switch (exitDirection) {
                case SOUTH -> velocity.multiply(-1,1,1);
                case EAST -> new Vec3d(velocity.getZ(), velocity.getY(), -velocity.getX());
                case WEST -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case UP -> new Vec3d(-velocity.getX(), velocity.getZ(), velocity.getY());
                case DOWN -> new Vec3d(-velocity.getX(), -velocity.getZ(), velocity.getY());
                default -> velocity.multiply(1, 1, -1);
            };
            case EAST -> switch (exitDirection) {
                case EAST -> velocity.multiply(1,1,-1);
                case NORTH -> new Vec3d(-velocity.getZ(), velocity.getY(), -velocity.getX());
                case SOUTH -> new Vec3d(-velocity.getZ(), velocity.getY(), velocity.getX());
                case UP -> new Vec3d(velocity.getY(), velocity.getX(), -velocity.getZ());
                case DOWN -> new Vec3d(velocity.getY(), -velocity.getX(), -velocity.getZ());
                default -> velocity.multiply(-1, 1, 1);
            };
            case WEST -> switch (exitDirection) {
                case WEST -> velocity.multiply(1,1,-1);
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

    public static List<Pair<Vec3d, BlockHitResult>> raycast(World world, RaycastContext context) {
        final List<Pair<Vec3d, BlockHitResult>> hits = new ArrayList<>();
        final Supplier<Entity> marker = Suppliers.memoize(() -> EntityType.MARKER.create(world));
        while (true) {
            final BlockHitResult result = world.raycast(context);
            hits.add(new Pair<>(context.getStart(), result));
            if (result.getType() == HitResult.Type.MISS) break;
            final double distance = context.getStart().distanceTo(context.getEnd());
            final Vec3d offset = result.getPos().subtract(context.getStart());
            final EntityHitResult portalHit = ProjectileUtil.raycast(
                marker.get(), context.getStart(), result.getPos(), Box.of(result.getPos(), 5, 5, 5),
                e -> e instanceof ExperimentalPortal, offset.lengthSquared()
            );
            if (portalHit == null) break;
            final ExperimentalPortal portal = (ExperimentalPortal)portalHit.getEntity();
            if (!portal.getActive()) break;
            final Direction facing = portal.getFacingDirection();
            final Direction otherFacing = Direction.fromVector(new BlockPos(portal.getOtherFacing()));
            final Vec3d newOffset = rotateVelocity(offset, facing, otherFacing)
                .multiply((distance - offset.length()) / offset.length());
            final Vec3d hitRelative = portalHit.getPos().subtract(portal.getOriginPos())
                .withAxis(facing.getAxis(), 0);
            final Vec3d newRel = rotateVelocity(hitRelative, facing, otherFacing);
            final Vec3d newStart = portal.getDestination().orElseThrow().add(newRel);
            //noinspection DataFlowIssue
            context = new RaycastContext(
                newStart, newStart.add(newOffset),
                ((RaycastContextAccessor)context).getShapeType(),
                ((RaycastContextAccessor)context).getFluid(),
                ((RaycastContextAccessor)context).getEntityPosition() instanceof EntityShapeContext esc
                    ? esc.getEntity() : null
            );
        }
        return hits;
    }

}
