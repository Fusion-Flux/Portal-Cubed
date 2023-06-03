package com.fusionflux.portalcubed.entity;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BlockCollisionLimiter {
    private final ThreadLocal<Tuple<Long, UUID>> lastCollidingEntity = new ThreadLocal<>();

    /**
     * Written by chylex and onehalf
     * Prevents handling collision for an entity multiple times if the entity is touching 2 or more blocks.
     * <p>
     * Because onEntityCollision is always called in succession for all blocks colliding with an entity,
     * it is enough to compare if either the world time or the entity has changed since last call (on the same thread).
     * <p>
     * Returns true if the collision should be handled.
     */
    public boolean check(Level world, Entity entity) {
        long currentWorldTime = world.getGameTime();
        Tuple<Long, UUID> last = this.lastCollidingEntity.get();
        if (last == null || last.getA() != currentWorldTime || !last.getB().equals(entity.getUUID())) {
            this.lastCollidingEntity.set(new Tuple<>(currentWorldTime, entity.getUUID()));
            return true;
        }
        return false;
    }
}
