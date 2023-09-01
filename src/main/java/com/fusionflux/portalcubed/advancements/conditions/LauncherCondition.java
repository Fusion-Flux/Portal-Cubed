package com.fusionflux.portalcubed.advancements.conditions;

import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LauncherCondition implements LootItemCondition {
	private final LocationPredicate location;
	private final DistancePredicate distance;

	public LauncherCondition(LocationPredicate location, DistancePredicate distance) {
		this.location = location;
		this.distance = distance;
	}

	@NotNull
	@Override
	public LootItemConditionType getType() {
		return PortalCubedConditions.LAUNCHER;
	}

	@NotNull
	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.THIS_ENTITY);
	}

	@Override
	public boolean test(LootContext lootContext) {
		final Entity thisEntity = lootContext.getParam(LootContextParams.THIS_ENTITY);
		final BlockPos launcher = PortalCubedComponents.ENTITY_COMPONENT.get(thisEntity).getLauncher();
		if (launcher == null) {
			return false;
		}
		if (!location.matches((ServerLevel)thisEntity.level(), launcher.getX() + 0.5, launcher.getY() + 0.5, launcher.getZ() + 0.5)) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (!distance.matches(
			thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(),
			launcher.getX() + 0.5, launcher.getY() + 0.5, launcher.getZ() + 0.5
		)) {
			return false;
		}
		return true;
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LauncherCondition> {
		@Override
		public void serialize(JsonObject json, LauncherCondition value, JsonSerializationContext serializationContext) {
			if (value.location != LocationPredicate.ANY) {
				json.add("location", value.location.serializeToJson());
			}
			if (value.distance != DistancePredicate.ANY) {
				json.add("distance", value.distance.serializeToJson());
			}
		}

		@NotNull
		@Override
		public LauncherCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
			return new LauncherCondition(
				LocationPredicate.fromJson(json.get("location")),
				DistancePredicate.fromJson(json.get("distance"))
			);
		}
	}
}
