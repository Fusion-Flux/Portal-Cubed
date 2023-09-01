package com.fusionflux.portalcubed.advancements.conditions;

import com.fusionflux.portalcubed.accessor.EntityExt;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FunnelCondition implements LootItemCondition {
	private final Boolean inFunnel;
	private final Boolean inCfg;

	public FunnelCondition(Boolean inFunnel, Boolean inCfg) {
		this.inFunnel = inFunnel;
		this.inCfg = inCfg;
	}

	@NotNull
	@Override
	public LootItemConditionType getType() {
		return PortalCubedConditions.FUNNEL;
	}

	@NotNull
	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.THIS_ENTITY);
	}

	@Override
	public boolean test(LootContext lootContext) {
		if (inFunnel != null && ((EntityExt)lootContext.getParam(LootContextParams.THIS_ENTITY)).isInFunnel() != inFunnel) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (inCfg != null && ((EntityExt)lootContext.getParam(LootContextParams.THIS_ENTITY)).cfg() != inCfg) {
			return false;
		}
		return true;
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FunnelCondition> {
		@Override
		public void serialize(JsonObject json, FunnelCondition value, JsonSerializationContext serializationContext) {
			if (value.inFunnel != null) {
				json.addProperty("in_funnel", value.inFunnel);
			}
			if (value.inCfg != null) {
				json.addProperty("in_cfg", value.inCfg);
			}
		}

		@NotNull
		@Override
		public FunnelCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
			return new FunnelCondition(
				getAsOptionalBoolean(json, "in_funnel"),
				getAsOptionalBoolean(json, "in_cfg")
			);
		}

		private static Boolean getAsOptionalBoolean(JsonObject json, String name) {
			return json.has(name) ? GsonHelper.convertToBoolean(json.get(name), name) : null;
		}
	}
}
