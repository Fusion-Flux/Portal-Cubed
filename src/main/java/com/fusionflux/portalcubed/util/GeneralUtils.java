package com.fusionflux.portalcubed.util;

import com.google.common.base.Suppliers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GeneralUtils {
    public record EntityRaycastTransform(
        Predicate<@NotNull Entity> hittable,
        Transform transform
    ) {
        @FunctionalInterface
        public interface Transform {
            @Nullable
            Pair<@NotNull Vec3d, @NotNull RaycastContext> transform(
                @NotNull RaycastContext context,
                @NotNull BlockHitResult blockHit,
                @NotNull EntityHitResult entityHit
            );
        }
    }

    public static List<Pair<Vec3d, Vec3d>> raycastWithEntityTransforms(World world, RaycastContext context, EntityRaycastTransform... transforms) {
        final List<Pair<Vec3d, Vec3d>> hits = new ArrayList<>();
        final Supplier<Entity> marker = Suppliers.memoize(() -> EntityType.MARKER.create(world));
        final Predicate<Entity> predicate = Arrays.stream(transforms)
            .map(EntityRaycastTransform::hittable)
            .reduce(Predicate::or)
            .orElse(null);
        mainLoop:
        while (true) {
            final BlockHitResult result = world.raycast(context);
            if (predicate == null) {
                hits.add(new Pair<>(context.getStart(), result.getPos()));
                break;
            }
            final Vec3d offset = result.getPos().subtract(context.getStart());
            final EntityHitResult hit = ProjectileUtil.raycast(
                marker.get(), context.getStart(), result.getPos(), new Box(context.getStart(), result.getPos()).expand(1),
                predicate, offset.lengthSquared()
            );
            if (hit == null) {
                hits.add(new Pair<>(context.getStart(), result.getPos()));
                break;
            }
            for (final var transform : transforms) {
                if (transform.hittable.test(hit.getEntity())) {
                    final Pair<Vec3d, RaycastContext> newContext = transform.transform.transform(context, result, hit);
                    if (newContext != null) {
                        hits.add(new Pair<>(context.getStart(), newContext.getLeft()));
                        context = newContext.getRight();
                        continue mainLoop;
                    }
                }
            }
            hits.add(new Pair<>(context.getStart(), result.getPos()));
            break;
        }
        return hits;
    }
}
