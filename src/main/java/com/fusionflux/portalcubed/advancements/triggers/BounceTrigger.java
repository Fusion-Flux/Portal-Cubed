package com.fusionflux.portalcubed.advancements.triggers;

import com.fusionflux.portalcubed.entity.EnergyPellet;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BounceTrigger extends SimpleCriterionTrigger<BounceTrigger.TriggerInstance> {
    public static final ResourceLocation ID = id("bounce");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext deserializationContext) {
        return new TriggerInstance(predicate, EntityPredicate.fromJson(json.get("pellet")));
    }

    public void trigger(ServerPlayer player, EnergyPellet pellet) {
        trigger(player, conditions -> conditions.matches(player.serverLevel(), player.position(), pellet));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate pellet;

        public TriggerInstance(ContextAwarePredicate player, EntityPredicate pellet) {
            super(ID, player);
            this.pellet = pellet;
        }

        public boolean matches(ServerLevel level, @Nullable Vec3 position, EnergyPellet pellet) {
            return this.pellet.matches(level, position, pellet);
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            final JsonObject result = super.serializeToJson(context);
            result.add("pellet", pellet.serializeToJson());
            return result;
        }
    }
}
