package com.fusionflux.portalcubed.advancements.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FlingTrigger extends SimpleCriterionTrigger<FlingTrigger.TriggerInstance> {
	public static final ResourceLocation ID = id("fling");

	@NotNull
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@NotNull
	@Override
	protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext deserializationContext) {
		return new TriggerInstance(
			predicate,
			LocationPredicate.fromJson(json.get("launcher")),
			DistancePredicate.fromJson(json.get("force"))
		);
	}

	public void trigger(ServerPlayer player, BlockPos launcherPos, Vec3 launcherForce) {
		trigger(player, conditions -> conditions.matches(player.serverLevel(), launcherPos, launcherForce));
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final LocationPredicate launcher;
		private final DistancePredicate force;

		public TriggerInstance(ContextAwarePredicate player, LocationPredicate launcher, DistancePredicate force) {
			super(ID, player);
			this.launcher = launcher;
			this.force = force;
		}

		public boolean matches(ServerLevel level, BlockPos launcherPos, Vec3 launcherForce) {
			return launcher.matches(level, launcherPos.getX() + 0.5, launcherPos.getY() + 0.5, launcherPos.getZ() + 0.5) &&
				force.matches(0, 0, 0, launcherForce.x, launcherForce.y, launcherForce.z);
		}

		@NotNull
		@Override
		public JsonObject serializeToJson(SerializationContext context) {
			final JsonObject result = super.serializeToJson(context);
			if (launcher != LocationPredicate.ANY) {
				result.add("launcher", launcher.serializeToJson());
			}
			if (force != DistancePredicate.ANY) {
				result.add("force", force.serializeToJson());
			}
			return result;
		}
	}
}
