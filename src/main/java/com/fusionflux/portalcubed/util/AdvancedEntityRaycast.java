package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdvancedEntityRaycast {
    public record TransformInfo(
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

    public record Result(
        List<Ray> rays
    ) {
        public record Ray(Vec3d start, Vec3d end, HitResult hit) {
            public Ray(Vec3d start, HitResult hit) {
                this(start, hit.getPos(), hit);
            }
        }

        public Result {
            Validate.isTrue(!rays.isEmpty(), "AdvancedEntityRaycast.Result must have at least one ray.");
            Validate.isTrue(rays.get(rays.size() - 1).hit instanceof BlockHitResult, "AdvancedEntityRaycast.Result.finalHit must be a BlockHitResult.");
            ((AdvancedRaycastResultHolder) rays.get(rays.size() - 1).hit).setResult(Optional.of(this));
        }

        public BlockHitResult finalHit() {
            return (BlockHitResult)rays.get(rays.size() - 1).hit;
        }
    }

    public static Result raycast(World world, RaycastContext context, TransformInfo... transforms) {
        final List<Result.Ray> hits = new ArrayList<>();
        final Supplier<Entity> marker = Suppliers.memoize(() -> EntityType.MARKER.create(world));
        final Entity[] lastEntity = new Entity[1];
        final Predicate<Entity> predicate = Arrays.stream(transforms)
            .map(TransformInfo::hittable)
            .reduce(Predicate::or)
            .map(p -> p.and(e -> e != lastEntity[0]))
            .orElse(null);
        BlockHitResult result;
        mainLoop:
        while (true) {
            result = world.raycast(context);
            if (predicate == null) break;
            final Vec3d offset = result.getPos().subtract(context.getStart());
            final EntityHitResult hit = ProjectileUtil.raycast(
                marker.get(), context.getStart(), result.getPos(), new Box(context.getStart(), result.getPos()).expand(1),
                predicate, offset.lengthSquared()
            );
            if (hit == null) break;
            for (final var transform : transforms) {
                if (transform.hittable.test(hit.getEntity())) {
                    final Pair<Vec3d, RaycastContext> newContext = transform.transform.transform(context, result, hit);
                    if (newContext != null) {
                        lastEntity[0] = hit.getEntity();
                        hits.add(new Result.Ray(context.getStart(), newContext.getLeft(), hit));
                        context = newContext.getRight();
                        continue mainLoop;
                    }
                }
            }
            break;
        }
        hits.add(new Result.Ray(context.getStart(), result));
        return new Result(hits);
    }

    public static RaycastContext withStartEnd(RaycastContext context, Vec3d start, Vec3d end) {
        //noinspection DataFlowIssue
        return new RaycastContext(
            start, end,
            ((RaycastContextAccessor)context).getShapeType(),
            ((RaycastContextAccessor)context).getFluid(),
            ((RaycastContextAccessor)context).getEntityPosition() instanceof EntityShapeContext esc
                ? esc.getEntity() : null
        );
    }
}
