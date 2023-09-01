package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.mixin.ClipContextAccessor;
import com.google.common.base.Suppliers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
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
			TransformResult transform(
				@NotNull ClipContext context,
				@NotNull BlockHitResult blockHit,
				@NotNull EntityHitResult entityHit
			);
		}
	}

	public record TransformResult(
		Vec3 prevHitPos,
		@Nullable ClipContext newContext,
		Set<Entity> ignoredEntities
	) {
		public TransformResult(Vec3 prevHitPos, @Nullable ClipContext newContext) {
			this(prevHitPos, newContext, Set.of());
		}
	}

	public record Result(
		List<Ray> rays
	) {
		public record Ray(Vec3 start, Vec3 end, HitResult hit) {
			public Ray(Vec3 start, HitResult hit) {
				this(start, hit.getLocation(), hit);
			}

			public Vec3 relative() {
				return end.subtract(start);
			}

			@Nullable
			public EntityHitResult entityRaycast(@NotNull Entity owner, Predicate<Entity> predicate) {
				return ProjectileUtil.getEntityHitResult(
					owner, start, end, new AABB(start, end).inflate(1), predicate, start.distanceToSqr(end)
				);
			}
		}

		public Result {
			Validate.isTrue(!rays.isEmpty(), "AdvancedEntityRaycast.Result must have at least one ray.");
		}

		public Ray finalRay() {
			return rays.get(rays.size() - 1);
		}

		public HitResult finalHit() {
			return finalRay().hit;
		}

		@Nullable
		public EntityHitResult entityRaycast(@NotNull Entity owner, Predicate<Entity> predicate) {
			for (final Ray ray : rays) {
				final EntityHitResult hit = ray.entityRaycast(owner, predicate);
				if (hit != null) return hit;
			}
			return null;
		}

		public double length() {
			double length = 0;
			for (final Ray ray : rays) {
				length += ray.start.distanceTo(ray.end);
			}
			return length;
		}
	}

	public static Result raycast(
		Level level,
		ClipContext context,
		BiFunction<Level, ClipContext, BlockHitResult> clipper,
		TransformInfo... transforms
	) {
		final List<Result.Ray> hits = new ArrayList<>();
		final Supplier<Entity> marker = Suppliers.memoize(() -> EntityType.MARKER.create(level));
		@SuppressWarnings("unchecked") Set<Entity>[] ignoredEntities = new Set[] {Set.of()};
		final Predicate<Entity> predicate = Arrays.stream(transforms)
			.map(TransformInfo::hittable)
			.reduce(Predicate::or)
			.map(p -> p.and(e -> !ignoredEntities[0].contains(e)))
			.orElse(null);
		BlockHitResult result;
		mainLoop:
		while (true) {
			result = clipper.apply(level, context);
			if (predicate == null) break;
			final Vec3 offset = result.getLocation().subtract(context.getFrom());
			final EntityHitResult hit = ProjectileUtil.getEntityHitResult(
				marker.get(), context.getFrom(), result.getLocation(), new AABB(context.getFrom(), result.getLocation()).inflate(1),
				predicate, offset.lengthSqr()
			);
			if (hit == null) break;
			for (final var transform : transforms) {
				if (transform.hittable.test(hit.getEntity())) {
					final TransformResult newContext = transform.transform.transform(context, result, hit);
					if (newContext != null) {
						ignoredEntities[0] = newContext.ignoredEntities;
						hits.add(new Result.Ray(context.getFrom(), newContext.prevHitPos, hit));
						context = newContext.newContext;
						if (context == null) {
							return new Result(hits);
						}
						continue mainLoop;
					}
				}
			}
			break;
		}
		hits.add(new Result.Ray(context.getFrom(), result));
		return new Result(hits);
	}

	public static ClipContext withStartEnd(ClipContext context, Vec3 start, Vec3 end) {
		//noinspection DataFlowIssue
		return new ClipContext(
			start, end,
			((ClipContextAccessor)context).getBlock(),
			((ClipContextAccessor)context).getFluid(),
			((ClipContextAccessor)context).getCollisionContext() instanceof EntityCollisionContext esc
				? esc.getEntity() : null
		);
	}
}
