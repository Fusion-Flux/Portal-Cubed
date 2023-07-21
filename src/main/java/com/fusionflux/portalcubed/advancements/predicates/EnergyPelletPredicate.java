package com.fusionflux.portalcubed.advancements.predicates;

import com.fusionflux.portalcubed.entity.EnergyPellet;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyPelletPredicate implements EntitySubPredicate {
    private final MinMaxBounds.Ints startingLife;
    private final MinMaxBounds.Ints life;
    private final MinMaxBounds.Ints bounces;
    private final EntityPredicate thrower;

    public EnergyPelletPredicate(MinMaxBounds.Ints startingLife, MinMaxBounds.Ints life, MinMaxBounds.Ints bounces, EntityPredicate thrower) {
        this.startingLife = startingLife;
        this.life = life;
        this.bounces = bounces;
        this.thrower = thrower;
    }

    public static EnergyPelletPredicate fromJson(JsonObject json) {
        return new EnergyPelletPredicate(
            MinMaxBounds.Ints.fromJson(json.get("starting_life")),
            MinMaxBounds.Ints.fromJson(json.get("life")),
            MinMaxBounds.Ints.fromJson(json.get("bounces")),
            EntityPredicate.fromJson(json.get("thrower"))
        );
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 pos) {
        if (!(entity instanceof EnergyPellet energyPellet)) {
            return false;
        }
        if (!startingLife.matches(energyPellet.getStartingLife())) {
            return false;
        }
        if (!life.matches(energyPellet.getLife())) {
            return false;
        }
        if (!bounces.matches(energyPellet.getBounces())) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!thrower.matches(level, pos, energyPellet.getThrower())) {
            return false;
        }
        return true;
    }

    @NotNull
    @Override
    public JsonObject serializeCustomData() {
        final JsonObject result = new JsonObject();
        if (!startingLife.isAny()) {
            result.add("starting_life", bounces.serializeToJson());
        }
        if (!life.isAny()) {
            result.add("life", bounces.serializeToJson());
        }
        if (!bounces.isAny()) {
            result.add("bounces", bounces.serializeToJson());
        }
        if (thrower != EntityPredicate.ANY) {
            result.add("thrower", thrower.serializeToJson());
        }
        return result;
    }

    @NotNull
    @Override
    public Type type() {
        return PortalCubedPredicates.ENERGY_PELLET;
    }
}
