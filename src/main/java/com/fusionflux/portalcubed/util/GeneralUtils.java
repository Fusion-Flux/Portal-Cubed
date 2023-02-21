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
import org.apache.commons.lang3.function.TriFunction;
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
        TriFunction<@NotNull RaycastContext, @NotNull BlockHitResult, @NotNull EntityHitResult, @Nullable RaycastContext> transform
    ) {
    }

    public static List<Pair<Vec3d, BlockHitResult>> raycastWithEntityTransforms(World world, RaycastContext context, EntityRaycastTransform... transforms) {
        final List<Pair<Vec3d, BlockHitResult>> hits = new ArrayList<>();
        final Supplier<Entity> marker = Suppliers.memoize(() -> EntityType.MARKER.create(world));
        final Predicate<Entity> predicate = Arrays.stream(transforms)
            .map(EntityRaycastTransform::hittable)
            .reduce(Predicate::or)
            .orElse(null);
        mainLoop:
        while (true) {
            final BlockHitResult result = world.raycast(context);
            hits.add(new Pair<>(context.getStart(), result));
            if (predicate == null) break;
            final Vec3d offset = result.getPos().subtract(context.getStart());
            final EntityHitResult hit = ProjectileUtil.raycast(
                marker.get(), context.getStart(), result.getPos(), Box.of(result.getPos(), 5, 5, 5),
                predicate, offset.lengthSquared()
            );
            if (hit == null) break;
            for (final var transform : transforms) {
                if (transform.hittable.test(hit.getEntity())) {
                    final RaycastContext newContext = transform.transform.apply(context, result, hit);
                    if (newContext != null) {
                        context = newContext;
                        continue mainLoop;
                    }
                }
            }
            break;
        }
        return hits;
    }
}
